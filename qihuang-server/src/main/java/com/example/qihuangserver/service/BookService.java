package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Book;
import com.example.qihuangserver.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getPaginatedBooks(int page, int size, String name, String author, String dynasty) {
        Pageable pageable = PageRequest.of(page, size);
        if ((name == null || name.isEmpty()) && (author == null || author.isEmpty()) && (dynasty == null || dynasty.isEmpty())) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.searchBooks(
                name == null ? null : name,
                author == null ? null : author,
                dynasty == null ? null : dynasty,
                pageable
        );
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }
}