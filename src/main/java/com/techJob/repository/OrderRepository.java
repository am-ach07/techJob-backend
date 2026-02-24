package com.techJob.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OrdersStatus;


public interface OrderRepository extends JpaRepository<Order, Long> {
	Boolean existsByClientAndServiceAndStatusIn(User client,ServiceOffer offer,List<OrdersStatus> status);
	Boolean existsByClientAndServiceAndStatus(User client,ServiceOffer offer,OrdersStatus status);
	Page<Order> findByClient(User client,Pageable pageable);
	Optional<Order> findByOrderPublicID(String orderPublicID);
	@Query("SELECT o FROM Order o WHERE o.service.artisan = :artisan")
	Page<Order> findByArtisan(@Param("artisan") ArtisanProfile artisan, Pageable pageable);

}