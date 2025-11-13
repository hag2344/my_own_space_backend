package com.nhs.myownspace.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(
        name = "user_info",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_provider_providerId", columnNames = {"provider","provider_id"})
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 서비스 제공자 (KAKAO, GOOGLE, NAVER 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    // 각 provider 내에서 유일한 사용자 ID
    @Column( name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image", length = 300)
    private String profileImage;


}
