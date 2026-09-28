package com.alejandro.iot.client;

import com.alejandro.iot.models.IotMonitoringServiceGrpc;
import com.alejandro.iot.models.SensorEvent;
import com.alejandro.iot.service.IotMonitoringService;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class IotClient {

    private final IotMonitoringServiceGrpc.IotMonitoringServiceStub stub;


    public IotClient(ManagedChannel channel) {
        this.stub = IotMonitoringServiceGrpc.newStub(channel);
    }

    public void startDemo() {
        SensorClientResponseObserver responseObserver = new SensorClientResponseObserver();

        StreamObserver<SensorEvent> requestObserver = stub.monitor(responseObserver);

        responseObserver.setRequestObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }
}
