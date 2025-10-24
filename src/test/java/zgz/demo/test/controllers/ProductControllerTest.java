package zgz.demo.test.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zgz.demo.test.mappers.ProductMapper;
import zgz.demo.test.models.ProductRequest;
import zgz.demo.test.models.ProductResponse;
import zgz.demo.test.services.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductServiceImpl productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    void getAll_returnsOkWithProductList() throws Exception {
        // Arrange
        List<ProductResponse> responses = List.of(
                new ProductResponse(1L, "Phone", "Nice phone", "electronics", new BigDecimal("299.99")),
                new ProductResponse(2L, "Chair", "Wooden chair", "furniture", new BigDecimal("89.50"))
        );
        Mockito.when(productService.findAll()).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Phone"))
                .andExpect(jsonPath("$[0].category").value("electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Chair"))
                .andExpect(jsonPath("$[1].category").value("furniture"));

        Mockito.verify(productService).findAll();
        Mockito.verifyNoMoreInteractions(productService);
    }

    @Test
    void getByCategory_returnsOkWithFilteredList() throws Exception {
        // Arrange
        String category = "electronics";
        List<ProductResponse> responses = List.of(
                new ProductResponse(10L, "Laptop", "Gaming", category, new BigDecimal("1299.00"))
        );
        Mockito.when(productService.findByCategory(category)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/products/category/{category}", category)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].category").value(category))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        Mockito.verify(productService).findByCategory(category);
        Mockito.verifyNoMoreInteractions(productService);
    }

    @Test
    void create_returnsOkWithCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest(
                "Keyboard", "Mechanical", "electronics", new BigDecimal("59.90")
        );
        ProductResponse created = new ProductResponse(
                100L, request.name(), request.description(), request.category(), request.price()
        );
        Mockito.when(productService.create(any(ProductRequest.class))).thenReturn(created);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                // NOTE: Controller returns 200 OK (not 201) per current implementation
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.category").value("electronics"));

        Mockito.verify(productService).create(any(ProductRequest.class));
        Mockito.verifyNoMoreInteractions(productService);
    }

    @Test
    void update_returnsOkWithUpdatedProduct() throws Exception {
        // Arrange
        long id = 5L;
        ProductRequest request = new ProductRequest(
                "Table", "Office table", "furniture", new BigDecimal("149.00")
        );
        ProductResponse updated = new ProductResponse(
                id, request.name(), request.description(), request.category(), request.price()
        );
        Mockito.when(productService.update(eq(id), any(ProductRequest.class))).thenReturn(updated);

        // Act & Assert
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.category").value("furniture"));

        Mockito.verify(productService).update(eq(id), any(ProductRequest.class));
        Mockito.verifyNoMoreInteractions(productService);
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        // Arrange
        long id = 7L;
        Mockito.doNothing().when(productService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(blankOrNullString()));

        Mockito.verify(productService).delete(id);
        Mockito.verifyNoMoreInteractions(productService);
    }
}
