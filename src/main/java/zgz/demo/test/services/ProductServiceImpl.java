package zgz.demo.test.services;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zgz.demo.test.mappers.ProductMapper;
import zgz.demo.test.models.ProductEntity;
import zgz.demo.test.models.ProductRequest;
import zgz.demo.test.models.ProductResponse;
import zgz.demo.test.repositories.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements BasicCrudService<ProductRequest, ProductResponse, Long> {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        ProductEntity entity = mapper.toEntity(productRequest);
        ProductEntity save = repository.save(entity);
        return mapper.toResponse(save);
    }

    @Override
    public List<ProductResponse> findAll() {
        List<ProductEntity> productEntities = repository.findAll();
        return mapper.toResponseList(productEntities);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest productRequest) {
        repository.findById(id).orElseThrow(() -> {
                     log.warn("Product with ID {} not found for update", id);
                     return new NoSuchElementException("Product with ID " + id + " not found");
                 });
        ProductEntity newProduct = mapper.toEntity(productRequest);
        newProduct.setId(id);
        ProductEntity save = repository.save(newProduct);
        return mapper.toResponse(save);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<ProductResponse> findByCategory(String category) {
        List<ProductEntity> productEntities = repository.findByCategory(category);
        return mapper.toResponseList(productEntities);
    }
}
