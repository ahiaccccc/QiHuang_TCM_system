package com.example.qihuangserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "classics", schema = "qihuangdb")
public class Classic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classic_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book  book;

    @Lob
    @Column(name = "original_text", nullable = false)
    private String originalText;

    @Lob
    @Column(name = "vernacular_text", nullable = false)
    private String vernacularText;

    @Lob
    @Column(name = "translate_text", nullable = false)
    private String translateText;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getVernacularText() {
        return vernacularText;
    }

    public void setVernacularText(String vernacularText) {
        this.vernacularText = vernacularText;
    }

    public String getTranslateText() {
        return translateText;
    }

    public void setTranslateText(String translateText) {
        this.translateText = translateText;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}