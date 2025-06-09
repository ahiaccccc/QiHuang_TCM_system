// UserRepository.java
package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}