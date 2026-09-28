package com.alejandro.test.iot;

import com.alejandro.common.GrpcServer;
import com.alejandro.iot.models.ErrorMessage;
import com.alejandro.iot.models.IotMonitoringServiceGrpc;
import com.alejandro.iot.models.ValidationCode;
import com.alejandro.iot.service.IotMonitoringService;
import com.alejandro.test.common.AbstractChannelTest;
import io.grpc.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

import static com.alejandro.iot.grpc.GrpcErrorMetadata.ERROR_MESSAGE_KEY;

public class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new IotMonitoringService());

    protected IotMonitoringServiceGrpc.IotMonitoringServiceStub stub;

    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.stub = IotMonitoringServiceGrpc.newStub(channel);
    }

    @AfterAll
    public void stop() {
        this.grpcServer.stop();
    }

    protected ErrorMessage getErrorMessage(Throwable t) {
        return Optional.ofNullable(Status.trailersFromThrowable(t))
                .map(metadata -> metadata.get(ERROR_MESSAGE_KEY))
                .orElseThrow();
    }

    protected ValidationCode getValidationCode(Throwable t) {
        return getErrorMessage(t).getValidationCode();
    }
}
