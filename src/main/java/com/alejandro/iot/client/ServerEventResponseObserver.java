package com.alejandro.iot.client;

import com.alejandro.iot.models.ErrorMessage;
import com.alejandro.iot.models.ServerEvent;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static com.alejandro.iot.grpc.GrpcErrorMetadata.ERROR_MESSAGE_KEY;

public class ServerEventResponseObserver implements StreamObserver<ServerEvent> {

    private static final Logger log = LoggerFactory.getLogger(ServerEventResponseObserver.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private StreamObserver<?> requestObserver;


    @Override
    public void onNext(ServerEvent serverEvent) {
        log.info("Server event received: {}", serverEvent);
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

    public void setRequestObserver(StreamObserver<?> requestObserver) {
        this.requestObserver = requestObserver;
    }

    public void await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
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
}
