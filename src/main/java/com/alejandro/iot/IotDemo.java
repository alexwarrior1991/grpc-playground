package com.alejandro.iot;

import com.alejandro.common.GrpcServer;
import com.alejandro.iot.client.IotClient;
import com.alejandro.iot.service.IotMonitoringService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class IotDemo {

    static void main() {
        GrpcServer server = GrpcServer.create(new IotMonitoringService());
        server.start();

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        try {
            IotClient client = new IotClient(channel);
            client.startDemo();
        } finally {
            channel.shutdownNow();
            server.stop();
        }
    }
}
