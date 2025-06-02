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
    public ResponseEntity<PageDTO<Classic>> getClassics(
            @PathVariable("bookId") Integer bookId,
            @RequestParam int page,
            @RequestParam int size) {

        Page<Classic> classics = classicService.getPaginatedClassicsByBook(bookId, page, size);
        return ResponseEntity.ok(PageDTO.fromPage(classics, classic -> classic));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classic> getClassicById(@PathVariable("id") Long id) {
        Classic classic = classicService.getClassicById(id);
        return ResponseEntity.ok(classic);
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

}

