package com.nhs.myownspace.user.entity;

import com.nhs.myownspace.user.Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_oauth",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_oauth_provider_pid",
                        columnNames = {"provider","provider_id"}
                )
        },
        indexes = {
                @Index(name="idx_oauth_user_id", columnList = "user_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserOauth {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    // 소셜 로그인 서비스 제공자 (KAKAO, GOOGLE, NAVER 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    // 각 provider 내에서 유일한 사용자 ID
    @Column( name = "provider_id", nullable = false, length = 100)
    private String providerId;
}
