package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.ExtraServiceOffer;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;

public interface ExtraServiceOfferRepository extends JpaRepository<ExtraServiceOffer, Long> {
	
	
	Optional<ExtraServiceOffer> findByTitle(String title);
	Optional<ExtraServiceOffer> findByExtraOfferPublicID(String extraOfferPublicID);
	
	
}
