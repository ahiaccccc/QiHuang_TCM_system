package com.example.qihuangserver.service;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRService {

    public String extractText(File imageFile) {
        ITesseract tesseract = new Tesseract();
        // 设置语言（中英文）
        tesseract.setLanguage("chi_sim+eng");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "识别失败：" + e.getMessage();
        }
    }
}
