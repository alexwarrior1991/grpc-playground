package com.alejandro.test.sec10;

import com.alejandro.common.GrpcServer;
import com.alejandro.models.sec10.BankServiceGrpc;
import com.alejandro.models.sec10.ErrorMessage;
import com.alejandro.models.sec10.ValidationCode;
import com.alejandro.sec10.BankService;
import com.alejandro.test.common.AbstractChannelTest;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

public abstract class AbstractTest extends AbstractChannelTest {

    private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());
    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceStub bankStub;
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    @BeforeAll
    public void setup() {
        this.grpcServer.start();
        this.bankStub = BankServiceGrpc.newStub(channel);
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    public void stop() {
        this.grpcServer.stop();
    }

    protected ValidationCode getValidationCode(Throwable t) {
        return Optional.ofNullable(Status.trailersFromThrowable(t))
                .map(m -> m.get(ERROR_MESSAGE_KEY))
                .map(ErrorMessage::getValidationCode)
                .orElseThrow();
    }
}
