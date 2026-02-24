package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByEmail(String email);
	public Boolean existsByEmail(String email);
	public Boolean existsByUsername(String username);
	public Boolean existsByPhoneNumber(String phoneNumber);
	public Optional<User>  findByUsername(String username);
	public Optional<User> findByUsernameOrEmail(String username, String email);
	public Optional<User> findByPublicID(String publicID);
	public Boolean existsByEmailAndPublicIDNot(String email, String publicID);
	public Boolean existsByUsernameOrEmail(String username, String email);
}
