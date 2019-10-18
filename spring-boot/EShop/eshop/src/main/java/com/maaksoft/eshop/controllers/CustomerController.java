package com.maaksoft.eshop.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import com.maaksoft.eshop.models.Customer;

import com.maaksoft.eshop.service.CustomerService;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public Iterable<Customer> getCustomers(HttpServletRequest request) {
        return customerService.getCustomers();
    }

    @PostMapping("/customers")
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @PutMapping("/customers/{customerId}")
    public Customer updateCustomer(@PathVariable("customerId") Long customerId, @RequestBody Customer customer) {
        customer.setId(customerId);
        return customerService.saveCustomer(customer);
    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Long customerId) {
        customerService.deleteCustomer(customerId);
    }

}