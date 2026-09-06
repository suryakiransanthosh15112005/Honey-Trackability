package com.honeychain.config;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.service.BlockchainService;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.demo-data.enabled:false}")
    private boolean demoDataEnabled;

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository,
                                      BeekeeperProfileRepository beekeeperProfileRepository,
                                      HiveRepository hiveRepository,
                                      HoneyBatchRepository honeyBatchRepository,
                                      BlockchainService blockchainService,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Base System Role Accounts for Authentication
            if (!userRepository.existsByPhoneNumber("9876543210")) {
                userRepository.save(new User("9876543210", passwordEncoder.encode("Admin@123"), Role.ADMIN, true));
                logger.info("Initialized default ADMIN user: phone=9876543210");
            }

            if (!userRepository.existsByPhoneNumber("9876543211")) {
                userRepository.save(new User("9876543211", passwordEncoder.encode("Kvic@123"), Role.KVIC_OFFICER, true));
                logger.info("Initialized default KVIC_OFFICER user: phone=9876543211");
            }

            if (!userRepository.existsByPhoneNumber("9876543212")) {
                userRepository.save(new User("9876543212", passwordEncoder.encode("Lab@123"), Role.LAB, true));
                logger.info("Initialized default LAB user: phone=9876543212");
            }

            User beekeeper = userRepository.findByPhoneNumber("9876543213").orElse(null);
            if (beekeeper == null) {
                beekeeper = new User("9876543213", passwordEncoder.encode("Beekeeper@123"), Role.BEEKEEPER, true);
                beekeeper = userRepository.save(beekeeper);
                logger.info("Initialized default BEEKEEPER user: phone=9876543213");
            }

            if (!userRepository.existsByPhoneNumber("9876543214")) {
                userRepository.save(new User("9876543214", passwordEncoder.encode("Customer@123"), Role.CUSTOMER, true));
                logger.info("Initialized default CUSTOMER user: phone=9876543214");
            }

            // 2. Demo Business Data (Disabled by default; only populated when app.demo-data.enabled=true)
            if (demoDataEnabled) {
                logger.info("DEMO_DATA_ENABLED=true: Seeding development business data (DEMO DATA)");

                BeekeeperProfile beekeeperProfile = beekeeperProfileRepository.findByUserId(beekeeper.getId()).orElse(null);
                if (beekeeperProfile == null) {
                    beekeeperProfile = new BeekeeperProfile(
                            beekeeper.getId(),
                            "KVIC-TN-2024-001",
                            "Ramesh Kumar",
                            "Kotagiri, Nilgiris",
                            null,
                            11.4200,
                            76.8800,
                            PreferredLanguage.TAMIL,
                            BeekeeperVerificationStatus.APPROVED
                    );
                    beekeeperProfile = beekeeperProfileRepository.save(beekeeperProfile);
                    logger.info("Initialized DEMO BEEKEEPER profile: KVIC-TN-2024-001 (Ramesh Kumar)");
                }

                if (hiveRepository.countByBeekeeperProfileId(beekeeperProfile.getId()) == 0) {
                    hiveRepository.save(new Hive(beekeeperProfile.getId(), "HIVE-0001",
                            "Nilgiris Cluster", 11.4200, 76.8800, HiveStatus.ACTIVE,
                            LocalDate.of(2024, 1, 15)));
                    hiveRepository.save(new Hive(beekeeperProfile.getId(), "HIVE-0002",
                            "Coimbatore Cluster", 11.0168, 76.9558, HiveStatus.ACTIVE,
                            LocalDate.of(2024, 3, 10)));
                    hiveRepository.save(new Hive(beekeeperProfile.getId(), "HIVE-0003",
                            "Ooty Cluster", 11.4102, 76.6950, HiveStatus.INACTIVE,
                            LocalDate.of(2024, 5, 20)));
                    logger.info("Initialized 3 DEMO hives for beekeeper profile: Ramesh Kumar");
                }

                if (honeyBatchRepository.countByBeekeeperProfileId(beekeeperProfile.getId()) == 0) {
                    List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(beekeeperProfile.getId());
                    if (!hives.isEmpty()) {
                        Hive hive1 = hives.get(0);
                        HoneyBatch b1 = honeyBatchRepository.save(new HoneyBatch(
                                "HC-2026-AB12CD34",
                                beekeeperProfile.getId(),
                                hive1.getId(),
                                LocalDate.of(2026, 8, 15),
                                new BigDecimal("8.50"),
                                null,
                                BatchStatus.CREATED
                        ));
                        blockchainService.recordBatch(b1);

                        if (hives.size() > 1) {
                            Hive hive2 = hives.get(1);
                            HoneyBatch b2 = honeyBatchRepository.save(new HoneyBatch(
                                    "HC-2026-EF56GH78",
                                    beekeeperProfile.getId(),
                                    hive2.getId(),
                                    LocalDate.of(2026, 8, 20),
                                    new BigDecimal("12.00"),
                                    null,
                                    BatchStatus.CREATED
                            ));
                            blockchainService.recordBatch(b2);

                            HoneyBatch b3 = honeyBatchRepository.save(new HoneyBatch(
                                    "HC-2026-99AABBCC",
                                    beekeeperProfile.getId(),
                                    hive1.getId(),
                                    LocalDate.of(2026, 8, 28),
                                    new BigDecimal("18.75"),
                                    null,
                                    BatchStatus.CREATED
                            ));
                            blockchainService.recordBatch(b3);
                        }
                        logger.info("Initialized 3 DEMO honey batches with blockchain records for beekeeper: Ramesh Kumar");
                    }
                }
            } else {
                logger.info("DEMO_DATA_ENABLED=false: No demo business records seeded. Database is pristine for real user-driven workflows.");
            }
        };
    }
}
