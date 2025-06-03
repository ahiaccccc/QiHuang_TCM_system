package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) OR " +
            "(:dynasty IS NULL OR LOWER(b.dynasty) LIKE LOWER(CONCAT('%', :dynasty, '%'))) ")
    Page<Book> searchBooks(
            @Param("name") String name,
            @Param("author") String author,
            @Param("dynasty") String dynasty,
            Pageable pageable
    );
}