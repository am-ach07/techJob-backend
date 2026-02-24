package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.OrderExtra;

public interface OrderExtraRepository extends JpaRepository<OrderExtra, Long> {
}
