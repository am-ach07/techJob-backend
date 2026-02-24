package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techJob.domain.entity.ArtisanProfile;

public interface ArtisanProfileRepository extends JpaRepository<ArtisanProfile, Long> {

	@Modifying
	@Query("""
	UPDATE ArtisanProfile a
	SET a.completedOrder = a.completedOrder + 1
	WHERE a.id = :id
	""")
	void incrementCompletedOrders(@Param("id") Long id);

	
	
}
