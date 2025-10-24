package zgz.demo.test.models;


import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        String category,
        BigDecimal price
) {}
