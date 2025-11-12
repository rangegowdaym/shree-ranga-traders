package com.shreerangatraders.service;

import com.shreerangatraders.entity.Customer;
import com.shreerangatraders.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByCustomerName(customer.getCustomerName())) {
            throw new RuntimeException("Customer with name '" + customer.getCustomerName() + "' already exists");
        }
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (!existing.getCustomerName().equals(customer.getCustomerName()) &&
            customerRepository.existsByCustomerName(customer.getCustomerName())) {
            throw new RuntimeException("Customer with name '" + customer.getCustomerName() + "' already exists");
        }

        existing.setCustomerName(customer.getCustomerName());
        existing.setContact(customer.getContact());
        existing.setAddress(customer.getAddress());

        return customerRepository.save(existing);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByName(String customerName) {
        return customerRepository.findByCustomerName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer not found with name: " + customerName));
    }
}

