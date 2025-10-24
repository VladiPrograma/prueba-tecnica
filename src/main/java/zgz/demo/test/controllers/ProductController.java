package zgz.demo.test.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

// OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import zgz.demo.test.mappers.ProductMapper;
import zgz.demo.test.models.ProductRequest;
import zgz.demo.test.models.ProductResponse;
import zgz.demo.test.services.ProductServiceImpl;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Products", description = "CRUD operations for products")
public class ProductController {

    private final ProductServiceImpl productService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns the full list of products"
    )
    @ApiResponse(responseCode = "200", description = "Products returned successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/category/{category}")
    @Operation(
            summary = "Get products by category",
            description = "Returns all products that belong to the specified category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "No products found for the given category", content = @Content)
    })
    public ResponseEntity<List<ProductResponse>> getByCategory(
            @Parameter(description = "Category name", example = "electronics")
            @PathVariable String category
    ) {
        List<ProductResponse> products = productService.findByCategory(category);
        return ResponseEntity.ok(products);
    }


    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Creates a product and returns it with its generated ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse productResponse = productService.create(request);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a product by ID",
            description = "Updates all fields of an existing product"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse update = productService.update(id, request);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product by ID",
            description = "Deletes an existing product"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
