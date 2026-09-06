package com.honeychain;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ConflictException;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.marketplace.dto.ProductCreateRequest;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.mapper.ProductMapper;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.marketplace.service.impl.ProductServiceImpl;
import com.honeychain.verification.service.PublicVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private HoneyBatchRepository batchRepository;
    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;
    @Mock
    private LabTestRepository labTestRepository;
    @Mock
    private PublicVerificationService verificationService;

    private ProductMapper productMapper;
    private ProductServiceImpl productService;

    private String phone = "9876543213";
    private Long profileId = 10L;
    private BeekeeperProfile profile;
    private HoneyBatch pureBatch;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper(labTestRepository, verificationService);
        productService = new ProductServiceImpl(
                productRepository,
                batchRepository,
                beekeeperProfileRepository,
                productMapper
        );

        profile = new BeekeeperProfile();
        profile.setId(profileId);
        profile.setName("Ravi Kumar");
        profile.setVillage("Kotagiri");

        pureBatch = new HoneyBatch();
        pureBatch.setId(100L);
        pureBatch.setBatchId("HC-2026-PURE1");
        pureBatch.setBeekeeperProfileId(profileId);
        pureBatch.setStatus(BatchStatus.PURE);
        pureBatch.setQuantityKg(new BigDecimal("10.00"));
        pureBatch.setHarvestDate(LocalDate.now());
    }

    @Test
    @DisplayName("Create product listing from valid PURE batch succeeds")
    void testCreateProductFromPureBatchSuccess() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(batchRepository.findByBatchId("HC-2026-PURE1")).thenReturn(Optional.of(pureBatch));
        when(productRepository.existsByBatchBatchId("HC-2026-PURE1")).thenReturn(false);

        ProductCreateRequest req = new ProductCreateRequest();
        req.setBatchId("HC-2026-PURE1");
        req.setProductName("Nilgiris Raw Honey");
        req.setFlowerSource("Wildflower");
        req.setRegion("Nilgiris");
        req.setPricePerKg(new BigDecimal("850.00"));
        req.setAvailableQuantityKg(new BigDecimal("8.50"));
        req.setDescription("Pure honey harvested from Nilgiris");

        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = productService.createProduct(req, phone);

        assertNotNull(response);
        assertEquals("Nilgiris Raw Honey", response.getProductName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product listing from FAILED batch is rejected with BadRequestException")
    void testCreateProductFromFailedBatchRejected() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));

        HoneyBatch failedBatch = new HoneyBatch();
        failedBatch.setBatchId("HC-2026-FAIL1");
        failedBatch.setBeekeeperProfileId(profileId);
        failedBatch.setStatus(BatchStatus.FAILED);
        when(batchRepository.findByBatchId("HC-2026-FAIL1")).thenReturn(Optional.of(failedBatch));

        ProductCreateRequest req = new ProductCreateRequest();
        req.setBatchId("HC-2026-FAIL1");
        req.setProductName("Failed Honey");
        req.setPricePerKg(new BigDecimal("500.00"));
        req.setAvailableQuantityKg(new BigDecimal("5.00"));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> productService.createProduct(req, phone));
        assertTrue(ex.getMessage().contains("not eligible"));
    }

    @Test
    @DisplayName("Create product listing from unowned batch is rejected")
    void testCreateProductFromUnownedBatchRejected() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));

        HoneyBatch unownedBatch = new HoneyBatch();
        unownedBatch.setBatchId("HC-2026-OTHER");
        unownedBatch.setBeekeeperProfileId(999L); // Other beekeeper
        unownedBatch.setStatus(BatchStatus.PURE);
        when(batchRepository.findByBatchId("HC-2026-OTHER")).thenReturn(Optional.of(unownedBatch));

        ProductCreateRequest req = new ProductCreateRequest();
        req.setBatchId("HC-2026-OTHER");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> productService.createProduct(req, phone));
        assertTrue(ex.getMessage().contains("do not own"));
    }

    @Test
    @DisplayName("Duplicate product listing for same batch is rejected with ConflictException (409)")
    void testDuplicateProductListingRejected() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(batchRepository.findByBatchId("HC-2026-PURE1")).thenReturn(Optional.of(pureBatch));
        when(productRepository.existsByBatchBatchId("HC-2026-PURE1")).thenReturn(true);

        ProductCreateRequest req = new ProductCreateRequest();
        req.setBatchId("HC-2026-PURE1");
        req.setProductName("Nilgiris Raw Honey");
        req.setPricePerKg(new BigDecimal("850.00"));
        req.setAvailableQuantityKg(new BigDecimal("8.50"));

        ConflictException ex = assertThrows(ConflictException.class, () -> productService.createProduct(req, phone));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Deactivate product listing sets isActive to false")
    void testDeactivateProductListing() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));

        Product product = new Product(pureBatch, profile, "Honey", "Wildflower", "Nilgiris",
                new BigDecimal("800.00"), new BigDecimal("5.00"), "Desc", null);
        product.setId(1L);
        product.setIsActive(true);

        when(productRepository.findByIdAndBeekeeperProfileId(1L, profileId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponse res = productService.updateProductStatus(1L, false, phone);

        assertNotNull(res);
        assertFalse(res.getIsActive());
    }
}
