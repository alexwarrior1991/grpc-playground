package com.alejandro.iot.client;

import com.alejandro.iot.models.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.alejandro.iot.grpc.GrpcErrorMetadata.ERROR_MESSAGE_KEY;

public class SensorClientResponseObserver implements StreamObserver<ServerEvent> {

    private static final Logger log = LoggerFactory.getLogger(SensorClientResponseObserver.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private final Iterator<SensorEvent> events;
    private StreamObserver<SensorEvent> requestObserver;

    public SensorClientResponseObserver() {

        this.events = List.of(
                validTemperatureEvent(),
                validHumidityEvent(),
                invalidBatteryEvent()
        ).iterator();
    }

    @Override
    public void onNext(ServerEvent serverEvent) {
        log.info("Server response: {}", serverEvent);
        sendNextEvent();
    }

    @Override
    public void onError(Throwable t) {
        Status status = Status.fromThrowable(t);
        Metadata trailers = Status.trailersFromThrowable(t);

        log.warn("Stream failed. code={}, description={}",
                status.getCode(),
                status.getDescription()
        );

        if (trailers != null) {
            ErrorMessage errorMessage = trailers.get(ERROR_MESSAGE_KEY);

            if (errorMessage != null) {
                logErrorMessage(errorMessage);
            }
        }

        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("Stream completed successfully");
        latch.countDown();
    }

    public void setRequestObserver(StreamObserver<SensorEvent> requestObserver) {
        this.requestObserver = requestObserver;
    }

    public void start() {
        sendNextEvent();
    }

    public void await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void sendNextEvent() {
        if (events.hasNext()) {
            SensorEvent event = events.next();
            log.info("Sending event: {}", event.getPayloadCase());
            requestObserver.onNext(event);
        } else {
            requestObserver.onCompleted();
        }
    }

    private void logErrorMessage(ErrorMessage errorMessage) {
        log.warn("requestId={}", errorMessage.getRequestId());
        log.warn("validationCode={}", errorMessage.getValidationCode());

        switch (errorMessage.getDetailsCase()) {
            case MISSING_FIELD -> {
                log.warn("detailType=missing_field");
                log.warn("field={}", errorMessage.getMissingField().getFieldName());
            }
            case NUMERIC_RANGE -> {
                log.warn("detailType=numeric_range");
                log.warn("field={}", errorMessage.getNumericRange().getFieldName());
                log.warn("actual={}", errorMessage.getNumericRange().getActualValue());
                log.warn("min={}", errorMessage.getNumericRange().getMinAllowed());
                log.warn("max={}", errorMessage.getNumericRange().getMaxAllowed());
            }
            case TIME_WINDOW -> {
                log.warn("detailType=time_window");
                log.warn("eventTimestamp={}", errorMessage.getTimeWindow().getEventTimestampEpochMillis());
                log.warn("serverTimestamp={}", errorMessage.getTimeWindow().getServerTimestampEpochMillis());
                log.warn("maxAgeMillis={}", errorMessage.getTimeWindow().getMaxAgeMillis());
            }
            case DETAILS_NOT_SET -> {
                log.warn("detailType=not_set");
            }
        }
    }


    private static SensorEvent validTemperatureEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setTemperature(
                        TemperatureReading.newBuilder()
                                .setCelsius(23.5)
                                .build()
                )
                .build();
    }

    private static SensorEvent validHumidityEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setHumidity(
                        HumidityReading.newBuilder()
                                .setPercentage(55.2)
                                .build()
                )
                .build();
    }

    private static SensorEvent invalidBatteryEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setStatus(
                        DeviceStatus.newBuilder()
                                .setFirmwareVersion("1.2.7")
                                .setBatteryPercentage(140)
                                .build()
                )
                .build();
    }
}
