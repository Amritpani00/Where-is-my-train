package com.traintracker.api.repo;

import com.traintracker.api.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByUsername(String username);
	Optional<UserAccount> findByEmail(String email);
}