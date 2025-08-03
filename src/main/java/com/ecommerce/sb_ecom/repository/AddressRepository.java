package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
