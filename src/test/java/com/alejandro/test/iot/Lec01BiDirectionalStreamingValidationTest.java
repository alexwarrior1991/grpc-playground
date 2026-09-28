package com.alejandro.test.iot;

import com.alejandro.iot.models.DeviceStatus;
import com.alejandro.iot.models.ErrorMessage;
import com.alejandro.iot.models.HumidityReading;
import com.alejandro.iot.models.PressureReading;
import com.alejandro.iot.models.SensorEvent;
import com.alejandro.iot.models.ServerEvent;
import com.alejandro.iot.models.TemperatureReading;
import com.alejandro.iot.models.ValidationCode;
import com.alejandro.test.common.ResponseObserver;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Lec01BiDirectionalStreamingValidationTest extends AbstractTest {

    @Test
    public void validStreamTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(validTemperatureEvent());
        requestObserver.onNext(validHumidityEvent());
        requestObserver.onNext(validPressureEvent());
        requestObserver.onNext(validDeviceStatusEvent());
        requestObserver.onCompleted();

        responseObserver.await();

        Assertions.assertNull(responseObserver.getThrowable());
        Assertions.assertEquals(4, responseObserver.getItems().size());

        Assertions.assertEquals("sensor-room-101", responseObserver.getItems().get(0).getDeviceId());
        Assertions.assertTrue(responseObserver.getItems().get(0).getAccepted());
        Assertions.assertTrue(responseObserver.getItems().get(0).getMessage().contains("TEMPERATURE"));

        Assertions.assertEquals("sensor-room-101", responseObserver.getItems().get(1).getDeviceId());
        Assertions.assertTrue(responseObserver.getItems().get(1).getAccepted());
        Assertions.assertTrue(responseObserver.getItems().get(1).getMessage().contains("HUMIDITY"));

        Assertions.assertEquals("sensor-room-101", responseObserver.getItems().get(2).getDeviceId());
        Assertions.assertTrue(responseObserver.getItems().get(2).getAccepted());
        Assertions.assertTrue(responseObserver.getItems().get(2).getMessage().contains("PRESSURE"));

        Assertions.assertEquals("sensor-room-101", responseObserver.getItems().get(3).getDeviceId());
        Assertions.assertTrue(responseObserver.getItems().get(3).getAccepted());
        Assertions.assertTrue(responseObserver.getItems().get(3).getMessage().contains("STATUS"));
    }

    @Test
    public void invalidBatteryTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(validTemperatureEvent());
        requestObserver.onNext(invalidBatteryEvent());

        responseObserver.await();

        Assertions.assertEquals(1, responseObserver.getItems().size());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Invalid battery percentage", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.INVALID_BATTERY, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.NUMERIC_RANGE, errorMessage.getDetailsCase());

        Assertions.assertEquals("status.battery_percentage", errorMessage.getNumericRange().getFieldName());
        Assertions.assertEquals(140.0, errorMessage.getNumericRange().getActualValue());
        Assertions.assertEquals(0.0, errorMessage.getNumericRange().getMinAllowed());
        Assertions.assertEquals(100.0, errorMessage.getNumericRange().getMaxAllowed());
    }

    @Test
    public void missingDeviceIdTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(missingDeviceIdEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Missing device id", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.MISSING_DEVICE_ID, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.MISSING_FIELD, errorMessage.getDetailsCase());

        Assertions.assertEquals("device_id", errorMessage.getMissingField().getFieldName());
    }

    @Test
    public void missingPayloadTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(missingPayloadEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Missing payload", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.MISSING_PAYLOAD, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.MISSING_FIELD, errorMessage.getDetailsCase());

        Assertions.assertEquals("payload", errorMessage.getMissingField().getFieldName());
    }

    @Test
    public void invalidTemperatureTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(invalidTemperatureEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Invalid temperature", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.INVALID_TEMPERATURE, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.NUMERIC_RANGE, errorMessage.getDetailsCase());

        Assertions.assertEquals("temperature.celsius", errorMessage.getNumericRange().getFieldName());
        Assertions.assertEquals(100.0, errorMessage.getNumericRange().getActualValue());
        Assertions.assertEquals(-40.0, errorMessage.getNumericRange().getMinAllowed());
        Assertions.assertEquals(85.0, errorMessage.getNumericRange().getMaxAllowed());
    }

    @Test
    public void invalidHumidityTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(invalidHumidityEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Invalid humidity", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.INVALID_HUMIDITY, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.NUMERIC_RANGE, errorMessage.getDetailsCase());

        Assertions.assertEquals("humidity.percentage", errorMessage.getNumericRange().getFieldName());
        Assertions.assertEquals(150.0, errorMessage.getNumericRange().getActualValue());
        Assertions.assertEquals(0.0, errorMessage.getNumericRange().getMinAllowed());
        Assertions.assertEquals(100.0, errorMessage.getNumericRange().getMaxAllowed());
    }

    @Test
    public void invalidPressureTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(invalidPressureEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Invalid pressure", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.INVALID_PRESSURE, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.NUMERIC_RANGE, errorMessage.getDetailsCase());

        Assertions.assertEquals("pressure.hpa", errorMessage.getNumericRange().getFieldName());
        Assertions.assertEquals(2000.0, errorMessage.getNumericRange().getActualValue());
        Assertions.assertEquals(300.0, errorMessage.getNumericRange().getMinAllowed());
        Assertions.assertEquals(1200.0, errorMessage.getNumericRange().getMaxAllowed());
    }

    @Test
    public void staleTimestampTest() {
        var responseObserver = ResponseObserver.<ServerEvent>create();

        StreamObserver<SensorEvent> requestObserver = this.stub.monitor(responseObserver);

        requestObserver.onNext(staleTimestampEvent());

        responseObserver.await();

        Assertions.assertTrue(responseObserver.getItems().isEmpty());
        Assertions.assertNotNull(responseObserver.getThrowable());

        var status = Status.fromThrowable(responseObserver.getThrowable());
        Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, status.getCode());
        Assertions.assertEquals("Stale sensor event", status.getDescription());

        ErrorMessage errorMessage = getErrorMessage(responseObserver.getThrowable());

        Assertions.assertFalse(errorMessage.getRequestId().isBlank());
        Assertions.assertEquals(ValidationCode.STALE_TIMESTAMP, errorMessage.getValidationCode());
        Assertions.assertEquals(ErrorMessage.DetailsCase.TIME_WINDOW, errorMessage.getDetailsCase());

        Assertions.assertTrue(errorMessage.getTimeWindow().getEventTimestampEpochMillis() > 0);
        Assertions.assertTrue(errorMessage.getTimeWindow().getServerTimestampEpochMillis() > 0);
        Assertions.assertEquals(300_000, errorMessage.getTimeWindow().getMaxAgeMillis());
    }

    private SensorEvent validTemperatureEvent() {
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

    private SensorEvent validHumidityEvent() {
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

    private SensorEvent validPressureEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setPressure(
                        PressureReading.newBuilder()
                                .setHpa(1013.25)
                                .build()
                )
                .build();
    }

    private SensorEvent validDeviceStatusEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setStatus(
                        DeviceStatus.newBuilder()
                                .setFirmwareVersion("1.2.7")
                                .setBatteryPercentage(80)
                                .build()
                )
                .build();
    }

    private SensorEvent invalidBatteryEvent() {
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

    private SensorEvent missingDeviceIdEvent() {
        return SensorEvent.newBuilder()
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setTemperature(
                        TemperatureReading.newBuilder()
                                .setCelsius(23.5)
                                .build()
                )
                .build();
    }

    private SensorEvent missingPayloadEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .build();
    }

    private SensorEvent invalidTemperatureEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setTemperature(
                        TemperatureReading.newBuilder()
                                .setCelsius(100.0)
                                .build()
                )
                .build();
    }

    private SensorEvent invalidHumidityEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setHumidity(
                        HumidityReading.newBuilder()
                                .setPercentage(150.0)
                                .build()
                )
                .build();
    }

    private SensorEvent invalidPressureEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis())
                .setPressure(
                        PressureReading.newBuilder()
                                .setHpa(2000.0)
                                .build()
                )
                .build();
    }

    private SensorEvent staleTimestampEvent() {
        return SensorEvent.newBuilder()
                .setDeviceId("sensor-room-101")
                .setTimestampEpochMillis(System.currentTimeMillis() - 600_000)
                .setTemperature(
                        TemperatureReading.newBuilder()
                                .setCelsius(23.5)
                                .build()
                )
                .build();
    }
}
