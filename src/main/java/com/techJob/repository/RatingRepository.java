package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.Rating;

import java.util.List;
import java.util.Optional;



public interface RatingRepository extends JpaRepository<Rating, Long> {

	Optional <Rating> findByOrder(Order order);
	boolean existsByOrder(Order order);
}
