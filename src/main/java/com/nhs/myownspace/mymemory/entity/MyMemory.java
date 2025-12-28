package com.nhs.myownspace.mymemory.entity;

import com.nhs.myownspace.user.Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "my_memory")
public class MyMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Column(name = "image_paths_json", columnDefinition = "TEXT")
    private String imagePathsJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}