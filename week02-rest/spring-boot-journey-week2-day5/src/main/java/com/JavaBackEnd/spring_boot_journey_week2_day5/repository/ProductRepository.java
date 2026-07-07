package com.JavaBackEnd.spring_boot_journey_week2_day5.repository;

import com.JavaBackEnd.spring_boot_journey_week2_day5.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final List<Product> products = new CopyOnWriteArrayList<>(List.of(
        new Product(idCounter.getAndIncrement(), "Laptop Pro", "Electronics", 1299.99, 50),
        new Product(idCounter.getAndIncrement(), "Wireless Mouse", "Electronics", 29.99, 200),
        new Product(idCounter.getAndIncrement(), "Standing Desk", "Furniture", 599.99, 30),
        new Product(idCounter.getAndIncrement(), "Coffee Mug", "Kitchen", 12.99, 500)
    ));

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
            products.add(product);
        } else {
            products.replaceAll(p -> p.getId().equals(product.getId()) ? product : p);
        }
        return product;
    }

    public boolean deleteById(Long id) {
        return products.removeIf(p -> p.getId().equals(id));
    }
}