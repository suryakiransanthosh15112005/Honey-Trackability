package com.honeychain.marketplace.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.marketplace.dto.ProductCreateRequest;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.dto.ProductUpdateRequest;
import com.honeychain.marketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beekeepers/products")
@Tag(name = "Beekeeper Marketplace Management", description = "Beekeeper product listing creation & management endpoints")
@PreAuthorize("hasRole('BEEKEEPER')")
public class BeekeeperProductController {

    private final ProductService productService;

    public BeekeeperProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product listing from a PURE honey batch")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            Authentication authentication) {
        String phone = authentication.getName();
        ProductResponse product = productService.createProduct(request, phone);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product listing created successfully", product));
    }

    @GetMapping
    @Operation(summary = "Get all product listings owned by authenticated beekeeper")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getMyProducts(
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication) {
        String phone = authentication.getName();
        PageResponse<ProductResponse> products = productService.getMyProducts(phone, pageable);
        return ResponseEntity.ok(ApiResponse.success("Beekeeper product listings retrieved successfully", products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single product listing owned by authenticated beekeeper")
    public ResponseEntity<ApiResponse<ProductResponse>> getMyProduct(
            @PathVariable Long id,
            Authentication authentication) {
        String phone = authentication.getName();
        ProductResponse product = productService.getMyProduct(id, phone);
        return ResponseEntity.ok(ApiResponse.success("Product listing retrieved successfully", product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product listing details")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            Authentication authentication) {
        String phone = authentication.getName();
        ProductResponse product = productService.updateProduct(id, request, phone);
        return ResponseEntity.ok(ApiResponse.success("Product listing updated successfully", product));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate a product listing")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            Authentication authentication) {
        String phone = authentication.getName();
        ProductResponse product = productService.updateProductStatus(id, active, phone);
        return ResponseEntity.ok(ApiResponse.success("Product status updated successfully", product));
    }
}
