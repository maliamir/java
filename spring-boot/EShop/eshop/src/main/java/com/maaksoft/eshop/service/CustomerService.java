package com.maaksoft.eshop.service;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import com.maaksoft.eshop.models.Customer;

import com.maaksoft.eshop.dao.CustomerDao;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    public Iterable<Customer> getCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomer(Long id) {
        return customerDao.findById(id).get();
    }

    public Customer saveCustomer(Customer customer) {

        System.out.println(customer);

        customerDao.save(customer);
        return customer;

    }

    public void deleteCustomer(Long id) {
        customerDao.deleteById(id);
    }

}