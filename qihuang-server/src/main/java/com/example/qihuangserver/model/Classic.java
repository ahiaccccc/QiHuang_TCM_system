package com.example.qihuangserver.model;

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

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "dynasty", nullable = false, length = 50)
    private String dynasty;

    @Lob
    @Column(name = "original_text", nullable = false)
    private String originalText;

    @Lob
    @Column(name = "vernacular_text", nullable = false)
    private String vernacularText;

    @Lob
    @Column(name = "translate_text", nullable = false)
    private String translateText;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "classic")
    private Set<Collected> collecteds = new LinkedHashSet<>();

    @Lob
    @Column(name = "category")
    private String category;

    @Lob
    @Column(name = "summary")
    private String summary;

    @OneToMany(mappedBy = "classic")
    @JsonManagedReference // 标记为正向引用
    private Set<QaSession> qaSessions = new LinkedHashSet<>();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Collected> getCollecteds() {
        return collecteds;
    }

    public void setCollecteds(Set<Collected> collecteds) {
        this.collecteds = collecteds;
    }

    public Set<QaSession> getQaSessions() {
        return qaSessions;
    }

    public void setQaSessions(Set<QaSession> qaSessions) {
        this.qaSessions = qaSessions;
    }
}