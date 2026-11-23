package com.connectly.repository;

import com.connectly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query(value = """
            SELECT *
            FROM users
            WHERE email <> :email
              AND enabled = true
            ORDER BY RANDOM()
            LIMIT :limit
            """, nativeQuery = true)
    List<User> findRandomUsers(
            @Param("email") String email,
            @Param("limit") int limit
    );
}