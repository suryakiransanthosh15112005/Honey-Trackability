package com.honeychain.marketplace.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.marketplace.dto.ProductFilterRequest;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Public Marketplace", description = "Public endpoints for browsing, searching, and inspecting verified honey listings")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Browse, search, and filter public verified honey products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getPublicProducts(
            @ModelAttribute ProductFilterRequest filter) {
        PageResponse<ProductResponse> products = productService.getPublicProducts(filter);
        return ResponseEntity.ok(ApiResponse.success("Public honey products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed product view with blockchain verification and beekeeper story")
    public ResponseEntity<ApiResponse<ProductResponse>> getPublicProductDetails(@PathVariable Long id) {
        ProductResponse product = productService.getPublicProductDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Product details retrieved successfully", product));
    }
}
