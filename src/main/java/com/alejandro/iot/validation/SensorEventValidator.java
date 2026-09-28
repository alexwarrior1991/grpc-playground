package com.alejandro.iot.validation;

import com.alejandro.iot.models.SensorEvent;

import java.time.Clock;
import java.util.Optional;

public class SensorEventValidator {

    private static final double MIN_TEMPERATURE = -40.0;
    private static final double MAX_TEMPERATURE = 85.0;

    private static final double MIN_HUMIDITY = 0.0;
    private static final double MAX_HUMIDITY = 100.0;

    private static final double MIN_PRESSURE = 300.0;
    private static final double MAX_PRESSURE = 1200.0;

    private static final int MIN_BATTERY = 0;
    private static final int MAX_BATTERY = 100;

    private static final long MAX_EVENT_AGE_MILLIS = 5 * 60 * 1000;

    private final Clock clock;

    public SensorEventValidator(Clock clock) {
        this.clock = clock;
    }

    public Optional<Throwable> validate(String requestId, SensorEvent event) {
        if (event.getDeviceId().isBlank()) {
            return Optional.of(IotErrorFactory.missingDeviceId(requestId));
        }

        if (event.getPayloadCase() == SensorEvent.PayloadCase.PAYLOAD_NOT_SET) {
            return Optional.of(IotErrorFactory.missingPayload(requestId));
        }

        Optional<Throwable> timestampError = validateTimestamp(requestId, event);
        if (timestampError.isPresent()) {
            return timestampError;
        }

        return switch (event.getPayloadCase()) {
            case TEMPERATURE -> validateTemperature(requestId, event.getTemperature().getCelsius());
            case HUMIDITY -> validateHumidity(requestId, event.getHumidity().getPercentage());
            case PRESSURE -> validatePressure(requestId, event.getPressure().getHpa());
            case STATUS -> validateBattery(requestId, event.getStatus().getBatteryPercentage());
            case PAYLOAD_NOT_SET -> Optional.of(IotErrorFactory.missingPayload(requestId));
        };
    }

    private Optional<Throwable> validateTimestamp(String requestId, SensorEvent event) {
        long serverTimestamp = clock.millis();
        long eventTimestamp = event.getTimestampEpochMillis();
        long age = serverTimestamp - eventTimestamp;

        if (age > MAX_EVENT_AGE_MILLIS) {
            return Optional.of(
                    IotErrorFactory.staleTimestamp(
                            requestId,
                            eventTimestamp,
                            serverTimestamp,
                            MAX_EVENT_AGE_MILLIS
                    )
            );
        }

        return Optional.empty();
    }

    private Optional<Throwable> validateTemperature(String requestId, double value) {
        if (value < MIN_TEMPERATURE || value > MAX_TEMPERATURE) {
            return Optional.of(
                    IotErrorFactory.invalidTemperature(
                            requestId,
                            value,
                            MIN_TEMPERATURE,
                            MAX_TEMPERATURE
                    )
            );
        }

        return Optional.empty();
    }

    private Optional<Throwable> validateHumidity(String requestId, double value) {
        if (value < MIN_HUMIDITY || value > MAX_HUMIDITY) {
            return Optional.of(
                    IotErrorFactory.invalidHumidity(
                            requestId,
                            value,
                            MIN_HUMIDITY,
                            MAX_HUMIDITY
                    )
            );
        }

        return Optional.empty();
    }

    private Optional<Throwable> validatePressure(String requestId, double value) {
        if (value < MIN_PRESSURE || value > MAX_PRESSURE) {
            return Optional.of(
                    IotErrorFactory.invalidPressure(
                            requestId,
                            value,
                            MIN_PRESSURE,
                            MAX_PRESSURE
                    )
            );
        }

        return Optional.empty();
    }

    private Optional<Throwable> validateBattery(String requestId, int value) {
        if (value < MIN_BATTERY || value > MAX_BATTERY) {
            return Optional.of(
                    IotErrorFactory.invalidBattery(
                            requestId,
                            value,
                            MIN_BATTERY,
                            MAX_BATTERY
                    )
            );
        }

        return Optional.empty();
    }
}
