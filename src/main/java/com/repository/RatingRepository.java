package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Rating;
import com.domain.entity.Order;
import java.util.List;
import java.util.Optional;



public interface RatingRepository extends JpaRepository<Rating, Long> {

	Optional <Rating> findByOrder(Order order);
	boolean existsByOrder(Order order);
}
