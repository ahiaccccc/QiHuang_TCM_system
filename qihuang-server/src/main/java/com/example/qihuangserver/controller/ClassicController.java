package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.ClassicSimpleDTO;
import com.example.qihuangserver.model.PageDTO;
import com.example.qihuangserver.service.ClassicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/classics")
public class ClassicController {
    @Autowired
    private ClassicService classicService;

    @GetMapping
    public ResponseEntity<PageDTO<ClassicSimpleDTO>> getClassics(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        Page<Classic> classics = classicService.getPaginatedClassics(page, size, category, keyword);
        return ResponseEntity.ok(PageDTO.fromPage(classics, ClassicSimpleDTO::fromEntity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classic> getClassicById(@PathVariable Long id) {
        return classicService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/autofill")
    public void autoFill() {
        classicService.autoClassifyAndSummarize();
    }

    @PostMapping
    public Classic create(@RequestBody Classic classic) {
        return classicService.save(classic);
    }

    @PutMapping("/{id}")
    public Classic update(@PathVariable Long id, @RequestBody Classic classic) {
        classic.setId(id);
        return classicService.save(classic);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        classicService.delete(id);
    }

    @GetMapping("/{classicId}")
    public ResponseEntity<Classic> getClassicDetail(@PathVariable Long classicId) {
        return ResponseEntity.ok(classicService.getClassicById(classicId));
    }
}

