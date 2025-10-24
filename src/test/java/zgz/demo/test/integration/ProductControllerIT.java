package zgz.demo.test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import zgz.demo.test.mappers.ProductMapper;
import zgz.demo.test.models.ProductEntity;
import zgz.demo.test.models.ProductResponse;
import zgz.demo.test.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb-it;DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        ProductEntity p1 = new ProductEntity();
        p1.setName("Phone X");
        p1.setDescription("Nice phone");
        p1.setCategory("electronics");
        p1.setPrice(new BigDecimal("299.99"));

        ProductEntity p2 = new ProductEntity();
        p2.setName("Chair A");
        p2.setDescription("Wooden chair");
        p2.setCategory("furniture");
        p2.setPrice(new BigDecimal("89.50"));

        ProductEntity p3 = new ProductEntity();
        p3.setName("Laptop Z");
        p3.setDescription("Gaming rig");
        p3.setCategory("electronics");
        p3.setPrice(new BigDecimal("1299.00"));

        productRepository.saveAll(List.of(p1, p2, p3));
    }

    @Test
    void getByCategory_electronics_returnsTwoItems() throws Exception {
        // Mock mapper to produce deterministic responses from entities
        when(productMapper.toResponseList(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            List<ProductEntity> entities = (List<ProductEntity>) invocation.getArgument(0);
            return entities.stream()
                    .map(e -> new ProductResponse(
                            e.getId(),
                            e.getName(),
                            e.getDescription(),
                            e.getCategory(),
                            e.getPrice()
                    ))
                    .toList();
        });

        mockMvc.perform(get("/products/category/{category}", "electronics")
                        .header("Referer", "/swagger-ui") // passes your SecurityConfig check
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Expect exactly the 2 electronics products
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].category", everyItem(is("electronics"))))
                // Check some fields
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", not(blankOrNullString())));
    }

    @Test
    void getByCategory_unknown_returnsEmptyArray() throws Exception {
        when(productMapper.toResponseList(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            List<ProductEntity> entities = (List<ProductEntity>) invocation.getArgument(0);
            return entities.stream()
                    .map(e -> new ProductResponse(
                            e.getId(),
                            e.getName(),
                            e.getDescription(),
                            e.getCategory(),
                            e.getPrice()
                    ))
                    .toList();
        });

        mockMvc.perform(get("/products/category/{category}", "toys")
                        .header("Referer", "/swagger-ui")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
