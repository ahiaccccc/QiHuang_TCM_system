package com.example.qihuangserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "msg_id", nullable = false)
    private Message msg;

    @Lob
    @Column(name = "file_type")
    private String fileType;

    @Size(max = 5123)
    @NotNull
    @Column(name = "file_url", nullable = false, length = 5123)
    private String fileUrl;

    @Column(name = "file_size")
    private Integer fileSize;

    @Size(max = 5123)
    @Column(name = "thumbnail_url", length = 5123)
    private String thumbnailUrl;

    @Column(name = "duration")
    private Integer duration;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}