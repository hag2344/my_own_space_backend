package com.nhs.myownspace.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user_info")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image", length = 300)
    private String profileImage;


}
