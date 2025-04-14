package com.example.qihuangserver.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "qa_messages", schema = "qihuangdb")
public class QaMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private QaMessage parent;

    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ColumnDefault("1")
    @Column(name = "is_current")
    private Boolean isCurrent;

    @ColumnDefault("'neutral'")
    @Lob
    @Column(name = "feedback")
    private String feedback;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference // 允许序列化正向关
    private Set<QaMessage> qaMessages = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference // 防止消息与会话循环引用
    private QaSession session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QaSession getSession() {
        return session;
    }

    public void setSession(QaSession session) {
        this.session = session;
    }

    public QaMessage getParent() {
        return parent;
    }

    public void setParent(QaMessage parent) {
        this.parent = parent;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<QaMessage> getQaMessages() {
        return qaMessages;
    }

    public void setQaMessages(Set<QaMessage> qaMessages) {
        this.qaMessages = qaMessages;
    }
}