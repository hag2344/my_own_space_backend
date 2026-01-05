package com.nhs.myownspace.bookreport.entity;

import com.nhs.myownspace.user.Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "book_report")
public class BookReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

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
