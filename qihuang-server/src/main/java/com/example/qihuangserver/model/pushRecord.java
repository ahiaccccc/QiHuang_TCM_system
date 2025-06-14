package com.example.qihuangserver.model;

import lombok.*;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "push_record")
public class PushRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "herb_id")
    private String herbId;

    @Column(name = "push_date")
    @Temporal(TemporalType.DATE)
    private LocalDate pushDate;

    @Column(name = "blessing", columnDefinition = "TEXT")
    private String blessing;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdTime;
}
