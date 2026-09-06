package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.qr.entity.QrCode;
import com.honeychain.qr.repository.QrCodeRepository;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QrGenerationIntegrationTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private JwtService jwtService;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private BeekeeperProfileRepository beekeeperProfileRepository;
        @Autowired
        private HiveRepository hiveRepository;
        @Autowired
        private HoneyBatchRepository honeyBatchRepository;
        @Autowired
        private QrCodeRepository qrCodeRepository;

        @Value("${app.upload.base-dir:target/test-uploads}")
        private String baseUploadDir;

        private final String BEEKEEPER_PHONE = "9876543213";
        private final String LAB_PHONE = "9876543212";

        private String beekeeperToken() {
                return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
        }

        private String labToken() {
                return jwtService.generateToken(LAB_PHONE, "LAB");
        }

        private String createAndVerifyPureBatch() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

                // 1. Create batch
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("12.50"));

                MvcResult createRes = mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn();

                String batchId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                                .at("/data/batchId").asText();

                // 2. Send for testing
                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/send-testing")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk());

                // 3. Lab submits PURE test
                LabTestCreateRequest labReq = new LabTestCreateRequest(98, LabTestResult.PURE,
                                "Pure wild forest honey");
                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(labReq)))
                                .andExpect(status().isCreated());

                return batchId;
        }

        // ── Test 1 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("1. PURE batch can generate QR code and ZXing decodes public verification URL (201)")
        void testGenerateQrCodeForPureBatch() throws Exception {
                String batchId = createAndVerifyPureBatch();

                MvcResult result = mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                                .andExpect(jsonPath("$.data.qrValue", containsString("/verify/" + batchId)))
                                .andExpect(jsonPath("$.data.qrImageUrl", is("/uploads/qr/" + batchId + ".png")))
                                .andReturn();

                // Verify HoneyBatch status updated to QR_GENERATED
                HoneyBatch batchInDb = honeyBatchRepository.findByBatchId(batchId).orElseThrow();
                assertEquals(BatchStatus.QR_GENERATED, batchInDb.getStatus());

                // Verify QrCode entity in database
                Optional<QrCode> qrOpt = qrCodeRepository.findByBatchId(batchId);
                assertTrue(qrOpt.isPresent());

                // Verify Real ZXing Image File Decoding
                Path qrFilePath = Paths.get(baseUploadDir, "qr", batchId + ".png").toAbsolutePath().normalize();
                File qrFile = qrFilePath.toFile();
                assertTrue(qrFile.exists(), "QR image file must exist on disk");

                BufferedImage bufferedImage = ImageIO.read(qrFile);
                assertNotNull(bufferedImage, "Image must be readable as PNG");
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                java.util.Map<com.google.zxing.DecodeHintType, Object> hints = new java.util.HashMap<>();
                hints.put(com.google.zxing.DecodeHintType.TRY_HARDER, Boolean.TRUE);
                hints.put(com.google.zxing.DecodeHintType.POSSIBLE_FORMATS,
                                java.util.List.of(com.google.zxing.BarcodeFormat.QR_CODE));
                com.google.zxing.qrcode.QRCodeReader reader = new com.google.zxing.qrcode.QRCodeReader();
                Result decodedResult = reader.decode(bitmap, hints);

                assertNotNull(decodedResult);
                assertTrue(decodedResult.getText().endsWith("/verify/" + batchId),
                                "Decoded QR content must match verification URL: " + decodedResult.getText());
        }

        // ── Test 2 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("2. Duplicate QR generation returns existing QR idempotently")
        void testDuplicateQrReturnsExisting() throws Exception {
                String batchId = createAndVerifyPureBatch();

                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isCreated());

                // Second call
                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.batchId", is(batchId)));
        }

        // ── Test 3 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("3. CREATED batch cannot generate QR (400 Bad Request)")
        void testCreatedBatchCannotGenerateQr() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

                HoneyBatch batch = honeyBatchRepository.save(new HoneyBatch(
                                "HC-2026-UNTESTED01", profile.getId(), hive.getId(),
                                LocalDate.now(), new BigDecimal("5.00"), null, BatchStatus.CREATED));

                mockMvc.perform(post("/api/beekeepers/batches/" + batch.getBatchId() + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message",
                                                containsString("only be generated for a purity-approved batch")));
        }

        // ── Test 4 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("4. FAILED batch cannot generate QR (400 Bad Request)")
        void testFailedBatchCannotGenerateQr() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

                HoneyBatch batch = honeyBatchRepository.save(new HoneyBatch(
                                "HC-2026-FAILED01", profile.getId(), hive.getId(),
                                LocalDate.now(), new BigDecimal("5.00"), null, BatchStatus.FAILED));

                mockMvc.perform(post("/api/beekeepers/batches/" + batch.getBatchId() + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isBadRequest());
        }

        // ── Test 5 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("5. Beekeeper can retrieve batch QR via GET (200)")
        void testGetBatchQrCode() throws Exception {
                String batchId = createAndVerifyPureBatch();

                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/generate-qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/beekeepers/batches/" + batchId + "/qr")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                                .andExpect(jsonPath("$.data.qrImageUrl", notNullValue()));
        }
}
