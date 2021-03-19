package com.flextech.building.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextech.building.authentication.AccessAuthenticationToken;
import com.flextech.building.aws.s3.FluxResponseProvider;
import com.flextech.building.aws.s3.S3ClientConfigurationProperties;
import com.flextech.building.aws.s3.UploadFailedException;
import com.flextech.building.common.model.FileData;
import com.flextech.building.common.webservice.InvalidInputException;
import com.flextech.building.entity.User;
import com.flextech.building.webservice.request.BlueprintAnalysisRequest;
import com.flextech.building.webservice.response.BlueprintAnalysisResponse;
import com.flextech.building.webservice.response.DesignUploadResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class BuildingService {

    private TikaConfig tika;

    @Autowired
    private S3AsyncClient s3client;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private S3ClientConfigurationProperties s3config;

    @Autowired
    private WebClient webClient;

    @Value("${husky.api.baseUrl}")
    private String huskyApiBaseUrl;

    public BuildingService() throws TikaException, IOException {
        tika = new TikaConfig();
    }

    private Mono<FileData> upload(FileData fileData) {
        Map<String, String> metadata = new HashMap<String, String>();
        if (fileData.getMediaType() == null) {
            fileData.setMediaType(MediaType.APPLICATION_OCTET_STREAM);
        }
        byte[] data = fileData.getData();
        if (fileData.getImage() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(fileData.getImage(), "png", baos);
            } catch (IOException e) {
                return Mono.error(e);
            }
            data = baos.toByteArray();
        }

        CompletableFuture<PutObjectResponse> future = s3client
                .putObject(PutObjectRequest.builder()
                                .bucket(s3config.getBucket())
                                .contentLength((long)data.length)
                                .key(fileData.getPath())
                                .contentType(fileData.getMediaType().toString())
                                .metadata(metadata)
                                .build(),
                        AsyncRequestBody.fromBytes(data));

        long start = System.currentTimeMillis();
        return Mono.fromFuture(future)
                .map(result -> {
                    checkResult(result);
                    long end = System.currentTimeMillis();
                    log.info("Upload Page " + fileData.getIndex() + " : " + (end - start) + " ms");
                    fileData.setData(null);
                    return fileData;
                });
    }

    /**
     * check result from an API call.
     * @param result Result from an API call
     */
    private void checkResult(SdkResponse result) {
        if (result.sdkHttpResponse() == null || !result.sdkHttpResponse().isSuccessful()) {
            throw new UploadFailedException(result.sdkHttpResponse().statusText().get());
        }
    }


    private String detectContentType(byte[] data) throws IOException {
        return tika.getDetector().detect(TikaInputStream.get(data), new Metadata()).toString();
    }

    private byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();

    }

    private Flux<FileData> generateRequireFiles(InputStream in, User user) {
        try {
            byte[] data = in.readAllBytes();
            String type = detectContentType(data);
            String groupId = UUID.randomUUID().toString();
            if (type.equals("application/pdf")) {
                final PDDocument document = PDDocument.load(data);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                return Flux.range(0, document.getNumberOfPages() + 1)
                    .parallel()
                    .runOn(Schedulers.parallel())
                    .map(page -> {
                        if (page < document.getNumberOfPages()) {
                            long start = System.currentTimeMillis();
                            BufferedImage bim = null;
                            try {
                                 bim = pdfRenderer.renderImageWithDPI(page, 400);
//                                bim = pdfRenderer.renderImage(page);
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                                throw new UncheckedIOException(e);
                            }
                            String fileName = UUID.randomUUID().toString();
                            FileData fileData = FileData.builder()
                                    .index(page)
                                    .image(bim)
                                    .path("designs/" + user.getId() + "/" + groupId + "/" + fileName + ".png")
                                    .url("content/" + groupId + "/" + fileName + ".png")
                                    .mediaType(MediaType.IMAGE_PNG).build();
                            long end = System.currentTimeMillis();
                            log.info("Page " + page + " : " + (end - start) + " ms");
                            return fileData;
                        } else {
                            String pdfFileName = UUID.randomUUID().toString();
                            FileData pdfFileData = FileData.builder()
                                    .index(document.getNumberOfPages())
                                    .data(data)
                                    .path("designs/" + user.getId() + "/" + groupId + "/" + pdfFileName + ".pdf")
                                    .url("content/" + groupId + "/" + pdfFileName + ".pdf")
                                    .mediaType(MediaType.APPLICATION_PDF)
                                    .build();
                            return pdfFileData;
                        }
                    }).sequential()
                        .collectList()
                        .flatMapMany(list -> Flux.fromIterable(list))
                        .parallel()
                        .flatMap(d -> upload(d))
                        .sorted(FileData::compareTo)
                        .doFinally( tmp -> {
                            try {
                                document.close();
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                            }
                        });


            } else if (type.equals("image/jpg") || type.equals("image/jpeg") || type.equals("image/png")) {
                MediaType mediaType = MediaType.parseMediaType(type);
                String fileName = UUID.randomUUID().toString();
                FileData fileData = FileData.builder()
                        .data(data)
                        .path("designs/" + user.getId() + "/" + groupId + "/" + fileName + "." + mediaType.getSubtype())
                        .url("content/" + groupId + "/" + fileName + "." + mediaType.getSubtype())
                        .mediaType(mediaType)
                        .build();
                return Flux.mergeSequential(upload(fileData));
            } else {
                throw new InvalidInputException(
                    messageSource.getMessage("error.validation.file.content.invalid", null, Locale.getDefault())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputException(e.getMessage());
        }
    }



    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/uploadDesign", consumes = "multipart/form-data")
    public Mono<DesignUploadResponse> uploadDesign(@RequestPart("file") FilePart file, Authentication auth) throws IOException {
        User user = (User)auth.getDetails();
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> dataBuffer.asInputStream())
                .flatMapMany(in -> this.generateRequireFiles(in, user))
                .collectList()
                .map(this::createUploadResponse);
    }

    private DesignUploadResponse createUploadResponse(List<FileData> dataList) {
        FileData fileData;
        if (dataList.size() > 1) {
            fileData = dataList.stream().filter(data -> data.getMediaType().getSubtype().equals("pdf"))
                    .findFirst().get();

        } else {
            fileData = dataList.get(0);
        }
        List<String> imageList = dataList.stream()
                .filter(data -> !data.getMediaType().getSubtype().equals("pdf"))
                .map(data -> data.getUrl())
                .collect(Collectors.toList());

        return DesignUploadResponse.builder()
                .path(fileData.getUrl())
                .mediaType(fileData.getMediaType().toString())
                .imageList(imageList)
                .build();
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping(value = "/content/{groupId}/{fileName}")
    public Mono<ResponseEntity<Flux<ByteBuffer>>> downloadContent(@PathVariable("groupId") String groupId,
                                            @PathVariable("fileName") String fileName, Authentication auth) throws IOException {
        User user = (User)auth.getDetails();
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3config.getBucket())
                .key("designs/" + user.getId() + "/" + groupId + "/" + fileName)
                .build();

        return Mono.fromFuture(s3client.getObject(request,new FluxResponseProvider()))
                .map(response -> {
                    checkResult(response.getSdkResponse());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, response.getSdkResponse().contentType())
                            .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.getSdkResponse().contentLength()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                            .body(response.getBufferFlux());
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                });
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping(value = "/result/content/**")
    public Mono<ResponseEntity<Flux<ByteBuffer>>> downloadResultContent(ServerHttpRequest request,
                                                                   Authentication auth) throws IOException {
        User user = (User)auth.getDetails();
        String remainingPath = new AntPathMatcher().extractPathWithinPattern("/result/content/**", request.getURI().getPath());
        GetObjectRequest s3Request = GetObjectRequest.builder()
                .bucket(s3config.getResultBucket())
                .key("/" + remainingPath)
                .build();

        return Mono.fromFuture(s3client.getObject(s3Request,new FluxResponseProvider()))
                .map(response -> {
                    checkResult(response.getSdkResponse());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, response.getSdkResponse().contentType())
                            .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.getSdkResponse().contentLength()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, response.getSdkResponse().contentDisposition())
                            .body(response.getBufferFlux());
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                });
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/blueprintAnalysis")
    public Mono<BlueprintAnalysisResponse> uploadDesign(@RequestBody BlueprintAnalysisRequest request, Authentication auth) throws IOException {
        AccessAuthenticationToken authToken = (AccessAuthenticationToken)auth;
        User user = (User)authToken.getDetails();
        return Mono.just(request)
                .map(req -> req.json())
                .doOnNext(json -> {
                    json.put("token", authToken.getTokenValue());
                    json.put("user_id", user.getId());
                    log.info(json.toString());
                })
                .flatMap(this::invokeHuskyBlueprintAnalysisAPI);
    }

    private Mono<BlueprintAnalysisResponse> invokeHuskyBlueprintAnalysisAPI(Map<String, Object> request) {
        String body = null;
        try {
            body = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return Mono.error(e);
        }
        return webClient.post()
                .uri(huskyApiBaseUrl + "/building-blueprint-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BlueprintAnalysisResponse.class);
//                .bodyToMono(String.class)
//                .map(this::createBlueprintAnalysisResponse);
//                .doOnNext(s -> log.info(s))
//                .map(s -> new BlueprintAnalysisResponse());

    }

    private BlueprintAnalysisResponse createBlueprintAnalysisResponse(String body) {
        try {
            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(body, BlueprintAnalysisResponse.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
