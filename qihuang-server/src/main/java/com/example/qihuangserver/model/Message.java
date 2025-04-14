package com.example.qihuangserver.model;

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
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conv_id", nullable = false)
    private Conversation conv;

    @Lob
    @Column(name = "role")
    private String role;

    @Lob
    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(name = "lang")
    private String lang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_msg_id")
    private Message parentMsg;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "msg")
    private Set<Attachment> attachments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parentMsg")
    private Set<Message> messages = new LinkedHashSet<>();

}