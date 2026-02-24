package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
