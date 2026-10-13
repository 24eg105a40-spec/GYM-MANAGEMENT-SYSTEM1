package com.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Connected Vehicle Telemetry System
 * Senior Academic Full-Stack Project - Spring Boot 3 Backend
 *
 * Provides RESTful telemetry management, time-series data storage,
 * autonomous periodic drift simulation, and threshold alert processing.
 */
@SpringBootApplication
@EnableScheduling
public class TelemetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryApplication.class, args);
    }
}
