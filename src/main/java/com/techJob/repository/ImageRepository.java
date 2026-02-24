package com.techJob.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Image;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;

public interface ImageRepository
extends JpaRepository<Image, Long> {

	List<Image> findByOffer(ServiceOffer offer);
	Optional<Image> findByImagePublicID(String imagePublicID);
	Optional<Image> findByOfferAndImagePublicID(ServiceOffer offer, String imagePublicID);
}
