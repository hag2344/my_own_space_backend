package com.nhs.myownspace;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    // 생성자 주입 스프링이 자동으로 UserRepository를 넣어줌
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // 전체 유저 조회 (GET)
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // 새 유저 추가 (POST)
    @PostMapping
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    // 유저 수정 (PUT)
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser){
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    return userRepository.save(user);
                })
                .orElseThrow(()->new RuntimeException("User not found with id " + id));
    }

    // 유저 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
    }


}
