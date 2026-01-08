package com.nhs.myownspace.bookreport.entity;

import com.nhs.myownspace.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "book_report",
        indexes = {
                @Index(name = "idx_book_report_user_id", columnList = "user_id")
        })
public class BookReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "book_name", nullable = false, length = 100)
    private String bookName;

    @Column(length = 100)
    private String publisher;

    @Column(length = 100)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String motive;

    @Column(columnDefinition = "TEXT")
    private String plot;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String realization;

    @Column(name = "image_path", columnDefinition = "TEXT")
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }


}
