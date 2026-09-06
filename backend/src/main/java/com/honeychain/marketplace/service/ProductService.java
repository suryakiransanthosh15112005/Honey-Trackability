package com.honeychain.marketplace.service;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.marketplace.dto.ProductCreateRequest;
import com.honeychain.marketplace.dto.ProductFilterRequest;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.dto.ProductUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request, String beekeeperPhone);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest request, String beekeeperPhone);

    ProductResponse updateProductStatus(Long productId, boolean isActive, String beekeeperPhone);

    PageResponse<ProductResponse> getMyProducts(String beekeeperPhone, Pageable pageable);

    ProductResponse getMyProduct(Long productId, String beekeeperPhone);

    PageResponse<ProductResponse> getPublicProducts(ProductFilterRequest filter);

    ProductResponse getPublicProductDetails(Long productId);
}
