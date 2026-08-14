package com.alejandro.sec06;

import com.alejandro.models.sec06.BalanceCheckRequest;
import com.alejandro.models.sec06.BankServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {

    private static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

    static void main() {

        var channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        var stub = BankServiceGrpc.newBlockingStub(channel);

        var balance = stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build());

        log.info("{}", balance);

    }
}
