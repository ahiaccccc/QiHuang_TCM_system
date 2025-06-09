package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Book;
import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.PageDTO;
import com.example.qihuangserver.service.BookService;
import com.example.qihuangserver.service.ClassicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private ClassicService classicService;

    @GetMapping
    public ResponseEntity<PageDTO<Book>> getBooks(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String dynasty) {

        Page<Book> books = bookService.getPaginatedBooks(page, size, name, author, dynasty);
        return ResponseEntity.ok(PageDTO.fromPage(books, book -> book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Integer id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    // 新增典籍相关接口
    @GetMapping("/{bookId}/classics")
    public ResponseEntity<PageDTO<Classic>> getClassicsByBook(
            @PathVariable("bookId") Integer bookId,
            @RequestParam int page,
            @RequestParam int size) {

        Page<Classic> classics = classicService.getPaginatedClassicsByBook(bookId, page, size);
        return ResponseEntity.ok(PageDTO.fromPage(classics, classic -> classic));
    }

    @PostMapping("/{bookId}/classics")
    public Classic createClassic(
            @PathVariable("bookId") Integer bookId,
            @RequestBody Classic classic) {

        Book book = bookService.getBookById(bookId); // 获取 Book 对象
        classic.setBook(book); // 设置关联
        return classicService.save(classic);
    }

    @PutMapping("/{bookId}/classics/{classicId}")
    public Classic updateClassic(
            @PathVariable("bookId") Integer bookId,
            @PathVariable("classicId") Long id,
            @RequestBody Classic classic) {

        Book book = bookService.getBookById(bookId); // 获取 Book 对象
        classic.setBook(book); // 设置关联
        classic.setId(id);
        return classicService.save(classic);
    }

    @DeleteMapping("/{bookId}/classics/{classicId}")
    public void deleteClassic(
            @PathVariable("bookId") Integer bookId,
            @PathVariable("classicId") Long id)  {

        classicService.delete(id);
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Integer id, @RequestBody Book book) {
        book.setId(id);
        return bookService.saveBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }
}