package com.totemsoft.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.User;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {

    @Modifying(flushAutomatically = true)
    @Query(
        value = "INSERT INTO oidc_user_authority (user_email, user_authority) VALUES (:email, :authority)",
        nativeQuery = true
    )
    int insertAuthority(@Param("email") String email, @Param("authority") String authority);

    @Modifying(flushAutomatically = true)
    @Query(
        value = "DELETE FROM oidc_user_authority WHERE user_email = :email AND user_authority = :authority",
        nativeQuery = true
    )
    int deleteAuthority(@Param("email") String email, @Param("authority") String authority);

}
