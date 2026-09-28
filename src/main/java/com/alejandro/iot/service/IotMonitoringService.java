package com.alejandro.iot.service;

import com.alejandro.iot.models.IotMonitoringServiceGrpc;
import com.alejandro.iot.models.SensorEvent;
import com.alejandro.iot.models.ServerEvent;
import com.alejandro.iot.validation.SensorEventValidator;
import io.grpc.stub.StreamObserver;

import java.time.Clock;

public class IotMonitoringService extends IotMonitoringServiceGrpc.IotMonitoringServiceImplBase {

    private final SensorEventValidator validator;


    public IotMonitoringService() {
        this.validator = new SensorEventValidator(Clock.systemUTC());
    }

    public IotMonitoringService(SensorEventValidator validator) {
        this.validator = validator;
    }

    @Override
    public StreamObserver<SensorEvent> monitor(StreamObserver<ServerEvent> responseObserver) {
        return new SensorEventRequestObserver(responseObserver, validator);
    }
}
