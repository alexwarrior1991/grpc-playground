package com.alejandro.sec06;

import com.alejandro.models.sec06.AccountBalance;
import com.alejandro.models.sec06.BalanceCheckRequest;
import com.alejandro.models.sec06.BankServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class GrpcClient {

    private static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

    static void main() throws InterruptedException {

        var channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        var stub = BankServiceGrpc.newStub(channel);

        stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build(), new StreamObserver<AccountBalance>() {
            @Override
            public void onNext(AccountBalance accountBalance) {
                log.info("{}", accountBalance);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                log.info("completed");
            }
        });

        log.info("done");
        Thread.sleep(Duration.ofSeconds(1));


    }
}
