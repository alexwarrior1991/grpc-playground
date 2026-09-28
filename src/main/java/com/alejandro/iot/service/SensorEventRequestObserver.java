package com.alejandro.iot.service;

import com.alejandro.iot.models.SensorEvent;
import com.alejandro.iot.models.ServerEvent;
import com.alejandro.iot.validation.SensorEventValidator;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SensorEventRequestObserver implements StreamObserver<SensorEvent> {

    private static final Logger log = LoggerFactory.getLogger(SensorEventRequestObserver.class);

    private final StreamObserver<ServerEvent> responseObserver;
    private final SensorEventValidator validator;
    private final String requestId;
    private final AtomicBoolean closed;

    public SensorEventRequestObserver(StreamObserver<ServerEvent> responseObserver, SensorEventValidator validator) {
        this.responseObserver = responseObserver;
        this.validator = validator;
        this.requestId = UUID.randomUUID().toString();
        this.closed = new AtomicBoolean(false);
    }

    @Override
    public void onNext(SensorEvent event) {
        if (closed.get()) {
            return;
        }

        validator.validate(requestId, event)
                .ifPresentOrElse(
                        this::closeWithError,
                        () -> sendAck(event)
                );
    }

    @Override
    public void onError(Throwable t) {
        if (closed.compareAndSet(false, true)) {
            log.warn("Client stream failed. requestId={}, error={}", requestId, t.getMessage());
        }
    }

    @Override
    public void onCompleted() {
        if (closed.compareAndSet(false, true)) {
            log.info("Client completed stream. requestId={}", requestId);
            responseObserver.onCompleted();
        }
    }

    private void sendAck(SensorEvent event) {
        ServerEvent serverEvent = ServerEvent.newBuilder()
                .setDeviceId(event.getDeviceId())
                .setAccepted(true)
                .setMessage("Accepted payload: " + event.getPayloadCase())
                .build();

        responseObserver.onNext(serverEvent);
    }

    private void closeWithError(Throwable error) {
        if (closed.compareAndSet(false, true)) {
            log.warn("Closing stream with validation error. requestId={}", requestId);
            responseObserver.onError(error);
        }
    }
}
