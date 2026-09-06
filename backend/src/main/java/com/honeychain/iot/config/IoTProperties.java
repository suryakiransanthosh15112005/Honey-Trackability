package com.honeychain.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Centralized IoT configuration properties.
 * All sensor thresholds and mock settings are defined here — never hardcoded in service logic.
 * This makes the system configurable for different environments and apiary conditions.
 *
 * IMPORTANT: These thresholds are prototype heuristics for MVP demonstration purposes.
 * They are not veterinary or scientific recommendations.
 */
@Component
@ConfigurationProperties(prefix = "iot")
public class IoTProperties {

    private Mock mock = new Mock();
    private Health health = new Health();

    public static class Mock {
        /** Whether mock IoT data generation is enabled */
        private boolean enabled = true;
        /** Interval in milliseconds between automatic sensor readings */
        private long intervalMs = 60000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getIntervalMs() { return intervalMs; }
        public void setIntervalMs(long intervalMs) { this.intervalMs = intervalMs; }
    }

    public static class Health {
        private Temperature temperature = new Temperature();
        private Humidity humidity = new Humidity();
        private Activity activity = new Activity();

        public static class Temperature {
            /** Upper bound of normal range (°C). Above this → WATCH */
            private double watchThreshold = 37.0;
            /** Above this → ALERT (°C) */
            private double alertThreshold = 39.0;
            /** Lower bound of normal range (°C). Below this → WATCH */
            private double lowerWatchThreshold = 30.0;
            /** Below this → ALERT (°C) */
            private double lowerAlertThreshold = 28.0;

            public double getWatchThreshold() { return watchThreshold; }
            public void setWatchThreshold(double watchThreshold) { this.watchThreshold = watchThreshold; }
            public double getAlertThreshold() { return alertThreshold; }
            public void setAlertThreshold(double alertThreshold) { this.alertThreshold = alertThreshold; }
            public double getLowerWatchThreshold() { return lowerWatchThreshold; }
            public void setLowerWatchThreshold(double lowerWatchThreshold) { this.lowerWatchThreshold = lowerWatchThreshold; }
            public double getLowerAlertThreshold() { return lowerAlertThreshold; }
            public void setLowerAlertThreshold(double lowerAlertThreshold) { this.lowerAlertThreshold = lowerAlertThreshold; }
        }

        public static class Humidity {
            /** Above this → WATCH (%) */
            private double watchThreshold = 70.0;
            /** Above this → ALERT (%) */
            private double alertThreshold = 80.0;
            /** Below this → WATCH (%) */
            private double lowerWatchThreshold = 45.0;
            /** Below this → ALERT (%) */
            private double lowerAlertThreshold = 35.0;

            public double getWatchThreshold() { return watchThreshold; }
            public void setWatchThreshold(double watchThreshold) { this.watchThreshold = watchThreshold; }
            public double getAlertThreshold() { return alertThreshold; }
            public void setAlertThreshold(double alertThreshold) { this.alertThreshold = alertThreshold; }
            public double getLowerWatchThreshold() { return lowerWatchThreshold; }
            public void setLowerWatchThreshold(double lowerWatchThreshold) { this.lowerWatchThreshold = lowerWatchThreshold; }
            public double getLowerAlertThreshold() { return lowerAlertThreshold; }
            public void setLowerAlertThreshold(double lowerAlertThreshold) { this.lowerAlertThreshold = lowerAlertThreshold; }
        }

        public static class Activity {
            /** Minimum score for HEALTHY (>=) */
            private int healthyMin = 70;
            /** Minimum score for WATCH (>=). Below this → ALERT */
            private int watchMin = 40;

            public int getHealthyMin() { return healthyMin; }
            public void setHealthyMin(int healthyMin) { this.healthyMin = healthyMin; }
            public int getWatchMin() { return watchMin; }
            public void setWatchMin(int watchMin) { this.watchMin = watchMin; }
        }

        public Temperature getTemperature() { return temperature; }
        public void setTemperature(Temperature temperature) { this.temperature = temperature; }
        public Humidity getHumidity() { return humidity; }
        public void setHumidity(Humidity humidity) { this.humidity = humidity; }
        public Activity getActivity() { return activity; }
        public void setActivity(Activity activity) { this.activity = activity; }
    }

    public Mock getMock() { return mock; }
    public void setMock(Mock mock) { this.mock = mock; }
    public Health getHealth() { return health; }
    public void setHealth(Health health) { this.health = health; }
}
