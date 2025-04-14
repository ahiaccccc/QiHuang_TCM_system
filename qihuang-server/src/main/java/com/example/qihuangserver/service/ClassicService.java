package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.repository.ClassicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ClassicService {
    @Autowired
    private ClassicRepository classicRepository;

    public Page<Classic> getPaginatedClassics(int page, int size, String category, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if ((category == null || category.equals("所有类型")) && (keyword == null || keyword.isEmpty())) {
            return classicRepository.findAll(pageable);
        }
        return classicRepository.search(category, keyword == null ? "" : keyword, pageable);
    }

    public void autoClassifyAndSummarize() {
        List<Classic> classics = classicRepository.findAll();
        for (Classic classic : classics) {
            if (classic.getCategory() == null || classic.getCategory().isEmpty()) {
                if (classic.getTitle().contains("方剂") || classic.getOriginalText().contains("汤")) {
                    classic.setCategory("中药方剂");
                } else if (classic.getTitle().contains("针灸") || classic.getOriginalText().contains("经络")) {
                    classic.setCategory("针灸推拿");
                } else if (classic.getTitle().contains("论")) {
                    classic.setCategory("基础理论");
                } else {
                    classic.setCategory("临床各科");
                }
            }
            if (classic.getSummary() == null || classic.getSummary().isEmpty()) {
                String text = classic.getOriginalText();
                classic.setSummary(text.length() > 100 ? text.substring(0, 100) + "..." : text);
            }
        }
        classicRepository.saveAll(classics);
    }

    public Optional<Classic> findById(Long id) {
        return classicRepository.findById(id);
    }

    public Classic save(Classic classic) {
        return classicRepository.save(classic);
    }

    public void delete(Long id) {
        classicRepository.deleteById(id);
    }

    public Classic getClassicById(Long classicId) {
        return classicRepository.getClassicById(classicId);
    }
}