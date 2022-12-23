package com.sid.billingservice;

import com.sid.billingservice.entities.Bill;
import com.sid.billingservice.entities.ProductItem;
import com.sid.billingservice.feign.CustomerRestClient;
import com.sid.billingservice.feign.ProductItemRestClient;
import com.sid.billingservice.model.Customer;
import com.sid.billingservice.model.Product;
import com.sid.billingservice.repository.BillRepository;
import com.sid.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(BillRepository billRepository,
                            ProductItemRepository productItemRepository,
                            CustomerRestClient customerRestClient,
                            ProductItemRestClient productItemRestClient){
        return args -> {
            Customer customer = customerRestClient.getCustomerById(1L);
            Bill bill = billRepository.save(new Bill(null, new Date(),null, customer.getId(), customer));
            PagedModel<Product> products = productItemRestClient.pageProducts();
            products.forEach(product -> {
                ProductItem productItem = new ProductItem();
                productItem.setPrice(product.getPrice());
                productItem.setQuantity(1+new Random().nextInt(100));
                productItem.setBill(bill);
                productItem.setProductId(product.getId());
                productItemRepository.save(productItem);
            });
            System.out.println(customer);
        };
    }
}
