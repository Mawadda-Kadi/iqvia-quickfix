package com.iqvia.quickfix.repository;

import com.iqvia.quickfix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
