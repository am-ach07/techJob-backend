package com.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.ArtisanProfile;
import com.domain.entity.ServiceOffer;
import com.domain.enums.OffersStatus;


public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {

	
	Optional <ServiceOffer> findByOfferPublicID(String offerPublicID);
	Page<ServiceOffer> findByArtisan(ArtisanProfile artisan,Pageable pageable);
	Optional<ServiceOffer> findByOfferPublicIDAndOffersStatus(String publicID, OffersStatus offersStatus);
	Page<ServiceOffer> findByOffersStatus(OffersStatus offersStatus,Pageable pageable);
	int deleteByOffersStatusAndDeletedAtBefore(OffersStatus offersStatus, LocalDateTime dateTime);

}