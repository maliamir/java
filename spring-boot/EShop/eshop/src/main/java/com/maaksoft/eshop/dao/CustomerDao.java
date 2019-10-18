package com.maaksoft.eshop.dao;

import org.springframework.stereotype.Repository;

import org.springframework.data.repository.CrudRepository;

import com.maaksoft.eshop.models.Customer;

@Repository
public interface CustomerDao extends CrudRepository<Customer, Long> {

}