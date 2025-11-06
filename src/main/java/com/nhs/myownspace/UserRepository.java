package com.nhs.myownspace;

import org.springframework.data.jpa.repository.JpaRepository;

// <user, Long> : User 엔티티를 관리하고, 기본키 타입은 Long
// JpaRepository를 상속하면 save(), findAll(), findById(), deleteById()
// 같은 CRUD 기능이 자동 제공됨.
public interface UserRepository extends JpaRepository<User,Long> {
}
