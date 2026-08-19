package com.alejandro.test.sec06;

import com.alejandro.common.GrpcServer;
import com.alejandro.models.sec06.BankServiceGrpc;
import com.alejandro.models.sec06.TransferServiceGrpc;
import com.alejandro.sec06.BankService;
import com.alejandro.sec06.TransferService;
import com.alejandro.test.common.AbstractChannelTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new BankService(), new TransferService());
    protected BankServiceGrpc.BankServiceStub bankStub;
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected TransferServiceGrpc.TransferServiceStub transferStub;

    @BeforeAll
    public void setup(){
        this.grpcServer.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankStub = BankServiceGrpc.newStub(channel);
        this.transferStub = TransferServiceGrpc.newStub(channel);
    }

    @AfterAll
    public void stop(){
        this.grpcServer.stop();
    }
}
