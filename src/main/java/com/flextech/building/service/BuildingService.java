package com.flextech.building.service;

import com.flextech.building.aws.s3.FluxResponseProvider;
import com.flextech.building.aws.s3.S3ClientConfigurationProperties;
import com.flextech.building.aws.s3.UploadFailedException;
import com.flextech.building.common.model.FileData;
import com.flextech.building.common.webservice.InvalidInputException;
import com.flextech.building.webservice.response.DesignUploadResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class BuildingService {

    private TikaConfig tika;

    @Autowired
    private S3AsyncClient s3client;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private S3ClientConfigurationProperties s3config;

    public BuildingService() throws TikaException, IOException {
        tika = new TikaConfig();
    }

    private Mono<FileData> upload(FileData fileData) {
        Map<String, String> metadata = new HashMap<String, String>();
        if (fileData.getMediaType() == null) {
            fileData.setMediaType(MediaType.APPLICATION_OCTET_STREAM);
        }
        CompletableFuture<PutObjectResponse> future = s3client
                .putObject(PutObjectRequest.builder()
                                .bucket(s3config.getBucket())
                                .contentLength((long)fileData.getData().length)
                                .key(fileData.getPath())
                                .contentType(fileData.getMediaType().toString())
                                .metadata(metadata)
                                .build(),
                        AsyncRequestBody.fromBytes(fileData.getData()));
        return Mono.fromFuture(future)
                .map(result -> {
                    checkResult(result);
                    fileData.setPath("content" + fileData.getPath().replace("designs", ""));
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

    private List<FileData> generateRequireFiles(InputStream in) {
        try {
            byte[] data = in.readAllBytes();
            String type = detectContentType(data);
            List<FileData> fileDataList = new ArrayList<>();
            String groupId = UUID.randomUUID().toString();
            if (type.equals("application/pdf")) {
                try (final PDDocument document = PDDocument.load(data)) {
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    for (int page = 0; page < document.getNumberOfPages(); ++page) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                        FileData fileData = FileData.builder()
                                .data(toByteArray(bim, "png"))
                                .path("designs/" + groupId + "/" + UUID.randomUUID().toString() + ".png")
                                .mediaType(MediaType.IMAGE_PNG).build();
                        fileDataList.add(fileData);
                    }
                }
                FileData fileData = FileData.builder()
                        .data(data)
                        .path("designs/" + groupId + "/" + UUID.randomUUID().toString() + ".pdf")
                        .mediaType(MediaType.APPLICATION_PDF)
                        .build();
                fileDataList.add(fileData);
            } else if (type.equals("image/jpg") || type.equals("image/jpeg") || type.equals("image/png")) {
                MediaType mediaType = MediaType.parseMediaType(type);
                FileData fileData = FileData.builder()
                        .data(data)
                        .path("designs/" + groupId + "/" + UUID.randomUUID().toString() + "." + mediaType.getSubtype())
                        .mediaType(mediaType)
                        .build();
                fileDataList.add(fileData);
            } else {
                throw new InvalidInputException(
                    messageSource.getMessage("error.validation.file.content.invalid", null, Locale.getDefault())
                );
            }
            return fileDataList;
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputException(e.getMessage());
        }
    }



    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/uploadDesign", consumes = "multipart/form-data")
    public Mono<DesignUploadResponse> uploadDesign(@RequestPart("file") FilePart file) throws IOException {
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> dataBuffer.asInputStream())
                .map(this::generateRequireFiles)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::upload)
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
                .map(data -> data.getPath())
                .collect(Collectors.toList());

        return DesignUploadResponse.builder()
                .path(fileData.getPath())
                .mediaType(fileData.getMediaType().toString())
                .imageList(imageList)
                .build();
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/content/{groupId}/{fileName}")
    public Mono<ResponseEntity<Flux<ByteBuffer>>> downloadContent(@PathVariable("groupId") String groupId,
                                            @PathVariable("fileName") String fileName) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3config.getBucket())
                .key("designs/" + groupId + "/" + fileName)
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
}
