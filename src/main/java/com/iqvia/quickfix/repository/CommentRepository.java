package com.iqvia.quickfix.repository;

import com.iqvia.quickfix.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
