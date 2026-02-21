package com.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.domain.entity.ArtisanProfile;
import com.domain.entity.Order;
import com.domain.entity.ServiceOffer;
import com.domain.entity.User;
import com.domain.enums.OrdersStatus;


public interface OrderRepository extends JpaRepository<Order, Long> {
	Boolean existsByClientAndServiceAndStatusIn(User client,ServiceOffer offer,List<OrdersStatus> status);
	Boolean existsByClientAndServiceAndStatus(User client,ServiceOffer offer,OrdersStatus status);
	Page<Order> findByClient(User client,Pageable pageable);
	Optional<Order> findByOrderPublicID(String orderPublicID);
	@Query("SELECT o FROM Order o WHERE o.service.artisan = :artisan")
	Page<Order> findByArtisan(@Param("artisan") ArtisanProfile artisan, Pageable pageable);

}