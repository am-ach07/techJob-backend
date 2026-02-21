package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.ExtraServiceOffer;

public interface ExtraServiceOfferRepository extends JpaRepository<ExtraServiceOffer, Long> {
	
	
	Optional<ExtraServiceOffer> findByTitle(String title);
	Optional<ExtraServiceOffer> findByExtraOfferPublicID(String extraOfferPublicID);
	
	
}
