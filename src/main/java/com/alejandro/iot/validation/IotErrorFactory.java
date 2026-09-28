package com.alejandro.iot.validation;

import com.alejandro.iot.models.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import static com.alejandro.iot.grpc.GrpcErrorMetadata.ERROR_MESSAGE_KEY;

public final class IotErrorFactory {

    private IotErrorFactory() {
    }

    public static StatusRuntimeException missingDeviceId(String requestId) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.MISSING_DEVICE_ID)
                .setMissingField(
                        MissingFieldError.newBuilder()
                                .setFieldName("device_id")
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Missing device id");
    }

    public static StatusRuntimeException missingPayload(String requestId) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.MISSING_PAYLOAD)
                .setMissingField(
                        MissingFieldError.newBuilder()
                                .setFieldName("payload")
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Missing payload");
    }

    public static StatusRuntimeException invalidTemperature(
            String requestId,
            double actual,
            double min,
            double max
    ) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.INVALID_TEMPERATURE)
                .setNumericRange(
                        NumericRangeError.newBuilder()
                                .setFieldName("temperature.celsius")
                                .setActualValue(actual)
                                .setMinAllowed(min)
                                .setMaxAllowed(max)
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Invalid temperature");
    }

    public static StatusRuntimeException invalidHumidity(
            String requestId,
            double actual,
            double min,
            double max
    ) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.INVALID_HUMIDITY)
                .setNumericRange(
                        NumericRangeError.newBuilder()
                                .setFieldName("humidity.percentage")
                                .setActualValue(actual)
                                .setMinAllowed(min)
                                .setMaxAllowed(max)
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Invalid humidity");
    }

    public static StatusRuntimeException invalidPressure(
            String requestId,
            double actual,
            double min,
            double max
    ) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.INVALID_PRESSURE)
                .setNumericRange(
                        NumericRangeError.newBuilder()
                                .setFieldName("pressure.hpa")
                                .setActualValue(actual)
                                .setMinAllowed(min)
                                .setMaxAllowed(max)
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Invalid pressure");
    }

    public static StatusRuntimeException invalidBattery(
            String requestId,
            int actual,
            int min,
            int max
    ) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.INVALID_BATTERY)
                .setNumericRange(
                        NumericRangeError.newBuilder()
                                .setFieldName("status.battery_percentage")
                                .setActualValue(actual)
                                .setMinAllowed(min)
                                .setMaxAllowed(max)
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Invalid battery percentage");
    }

    public static StatusRuntimeException staleTimestamp(
            String requestId,
            long eventTimestamp,
            long serverTimestamp,
            long maxAgeMillis
    ) {
        ErrorMessage errorMessage = ErrorMessage.newBuilder()
                .setRequestId(requestId)
                .setValidationCode(ValidationCode.STALE_TIMESTAMP)
                .setTimeWindow(
                        TimeWindowError.newBuilder()
                                .setEventTimestampEpochMillis(eventTimestamp)
                                .setServerTimestampEpochMillis(serverTimestamp)
                                .setMaxAgeMillis(maxAgeMillis)
                                .build()
                )
                .build();

        return invalidArgument(errorMessage, "Stale sensor event");
    }

    private static StatusRuntimeException invalidArgument(
            ErrorMessage errorMessage,
            String description
    ) {
        Metadata metadata = new Metadata();
        metadata.put(ERROR_MESSAGE_KEY, errorMessage);

        return Status.INVALID_ARGUMENT
                .withDescription(description)
                .asRuntimeException(metadata);
    }
}
