package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Classic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassicRepository extends JpaRepository<Classic, Long> {
    Page<Classic> findAll(Pageable pageable);
    Page<Classic> findByBookId(Integer bookId, Pageable pageable);
//    Page<Classic> findByCategory(String category, Pageable pageable);
//    @Query("SELECT c FROM Classic c WHERE (:category IS NULL OR c.category = :category) AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Classic> search(@Param("category") String category, @Param("keyword") String keyword, Pageable pageable);

}