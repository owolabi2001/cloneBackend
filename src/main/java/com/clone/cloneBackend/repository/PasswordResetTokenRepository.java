package com.clone.cloneBackend.repository;


import com.clone.cloneBackend.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    PasswordResetToken findPasswordResetTokenByToken(String token);
}
