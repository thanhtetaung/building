package com.flextech.building.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BuildingService {

    public static void main(String[] args) throws IOException {
        try (final PDDocument document = PDDocument.load(new File("/Users/thanhtetaung/Downloads/Pauk-အသုံးပြုပုံ-compressed.pdf"))){
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page)
            {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String fileName = "/Users/thanhtetaung/Desktop/pdf-images/" + "image-" + page + ".png";
                ImageIOUtil.writeImage(bim, fileName, 300);
            }
        }
    }

}
