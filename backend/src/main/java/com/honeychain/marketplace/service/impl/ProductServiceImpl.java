package com.honeychain.marketplace.service.impl;

import java.util.List;
import java.util.Optional;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ConflictException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.marketplace.dto.ProductCreateRequest;
import com.honeychain.marketplace.dto.ProductFilterRequest;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.dto.ProductUpdateRequest;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.mapper.ProductMapper;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.marketplace.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Set<BatchStatus> ALLOWED_LISTING_STATUSES = EnumSet.of(
            BatchStatus.PURE,
            BatchStatus.QR_GENERATED,
            BatchStatus.IN_STOCK
    );

    private final ProductRepository productRepository;
    private final HoneyBatchRepository batchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              HoneyBatchRepository batchRepository,
                              BeekeeperProfileRepository beekeeperProfileRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request, String beekeeperPhone) {
        BeekeeperProfile profile = getBeekeeperProfile(beekeeperPhone);

        HoneyBatch batch = batchRepository.findByBatchId(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Honey batch not found with ID: " + request.getBatchId()));

        // Ownership check
        if (!batch.getBeekeeperProfileId().equals(profile.getId())) {
            throw new BadRequestException("You do not own this honey batch.");
        }

        // Status check: ONLY PURE, QR_GENERATED, IN_STOCK
        if (!ALLOWED_LISTING_STATUSES.contains(batch.getStatus())) {
            throw new BadRequestException("Batch status '" + batch.getStatus() + "' is not eligible for marketplace listing. Only PURE batches can be listed.");
        }

        // One batch = One product listing check
        if (productRepository.existsByBatchBatchId(batch.getBatchId())) {
            throw new ConflictException("A product listing already exists for batch ID: " + request.getBatchId());
        }

        // Price & Quantity checks
        if (request.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per kg must be greater than zero.");
        }
        if (request.getAvailableQuantityKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Available quantity must be greater than zero.");
        }

        Product product = new Product(
                batch,
                profile,
                request.getProductName(),
                request.getFlowerSource(),
                request.getRegion(),
                request.getPricePerKg(),
                request.getAvailableQuantityKg(),
                request.getDescription(),
                request.getImageUrl()
        );

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, String beekeeperPhone) {
        BeekeeperProfile profile = getBeekeeperProfile(beekeeperPhone);
        Product product = productRepository.findByIdAndBeekeeperProfileId(productId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product listing not found or access denied for ID: " + productId));

        if (request.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per kg must be greater than zero.");
        }
        if (request.getAvailableQuantityKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Available quantity must be greater than zero.");
        }

        product.setProductName(request.getProductName());
        product.setFlowerSource(request.getFlowerSource());
        product.setRegion(request.getRegion());
        product.setPricePerKg(request.getPricePerKg());
        product.setAvailableQuantityKg(request.getAvailableQuantityKg());
        product.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    public ProductResponse updateProductStatus(Long productId, boolean isActive, String beekeeperPhone) {
        BeekeeperProfile profile = getBeekeeperProfile(beekeeperPhone);
        Product product = productRepository.findByIdAndBeekeeperProfileId(productId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product listing not found or access denied for ID: " + productId));

        product.setIsActive(isActive);
        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getMyProducts(String beekeeperPhone, Pageable pageable) {
        Optional<BeekeeperProfile> profileOpt = beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone);
        if (profileOpt.isEmpty()) {
            return PageResponse.of(Page.empty(pageable), List.of());
        }
        BeekeeperProfile profile = profileOpt.get();
        Page<Product> page = productRepository.findAllByBeekeeperProfileId(profile.getId(), pageable);
        List<ProductResponse> content = page.getContent().stream().map(productMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getMyProduct(Long productId, String beekeeperPhone) {
        BeekeeperProfile profile = getBeekeeperProfile(beekeeperPhone);
        Product product = productRepository.findByIdAndBeekeeperProfileId(productId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product listing not found or access denied for ID: " + productId));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getPublicProducts(ProductFilterRequest filter) {
        // Safe whitelist sorting
        Sort sort;
        if ("priceAsc".equalsIgnoreCase(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.ASC, "pricePerKg");
        } else if ("priceDesc".equalsIgnoreCase(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "pricePerKg");
        } else if ("ratingDesc".equalsIgnoreCase(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "id"));
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(Math.max(0, filter.getPage()), Math.max(1, filter.getSize()), sort);

        Page<Product> page = productRepository.findPublicProducts(
                filter.getSearch(),
                filter.getRegion(),
                filter.getFlowerSource(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getMinRating(),
                pageable
        );

        List<ProductResponse> content = page.getContent().stream().map(productMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getPublicProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw new ResourceNotFoundException("Product is currently unavailable.");
        }

        return productMapper.toResponse(product);
    }

    private BeekeeperProfile getBeekeeperProfile(String beekeeperPhone) {
        return beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found for phone: " + beekeeperPhone));
    }
}
