package zgz.demo.test.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import zgz.demo.test.models.ProductEntity;
import zgz.demo.test.models.ProductRequest;
import zgz.demo.test.models.ProductResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    //@Todo inyectar
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductEntity toEntity(ProductRequest request);

    ProductResponse toResponse(ProductEntity product);

    List<ProductResponse> toResponseList(List<ProductEntity> products);
}
