package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.ClassicSimpleDTO;
import com.example.qihuangserver.repository.ClassicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ClassicService {
    @Autowired
    private ClassicRepository classicRepository;


    public Classic save(Classic classic) {
        return classicRepository.save(classic);
    }

    public void delete(Long id) {
        classicRepository.deleteById(id);
    }

    public Classic getClassicById(Long id) {
        Optional<Classic> optionalClassic = classicRepository.findById(id);
        return optionalClassic.orElse(null);
    }

    public Page<Classic> getPaginatedClassicsByBook(Integer bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return classicRepository.findByBookId(bookId, pageable);
    }
}