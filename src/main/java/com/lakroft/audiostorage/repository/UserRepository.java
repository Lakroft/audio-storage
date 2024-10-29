package com.lakroft.audiostorage.repository;

import com.lakroft.audiostorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
