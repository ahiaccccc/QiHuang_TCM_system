package com.example.qihuangserver.service;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileProcessingService {

    // 设置Tesseract数据路径，指向你的tessdata目录
    private static final String TESSDATA_PATH = "D:/software/tesseract-ocr/tessdata";

    public String extractText(MultipartFile file) throws IOException, TesseractException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("ocr-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        // 初始化Tesseract OCR
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setLanguage("chi_sim+eng"); // 中文简体+英文

        // 识别文本
        String result;
        try {
            BufferedImage image = ImageIO.read(tempFile.toFile());
            result = tesseract.doOCR(image);
        } finally {
            // 删除临时文件
            Files.deleteIfExists(tempFile);
        }

        return result;
    }
}