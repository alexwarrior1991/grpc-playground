package com.alejandro.sec09;

import com.alejandro.models.sec09.*;
import com.alejandro.sec09.repository.AccountRepository;
import com.alejandro.sec09.validator.RequestValidator;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class BankService extends com.alejandro.models.sec09.BankServiceGrpc.BankServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .map(status -> status.asRuntimeException())
                .ifPresentOrElse(
                        e -> responseObserver.onError(e),
                        () -> sendAccountBalance(request, responseObserver)
                );
    }

    private void sendAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .or(() -> RequestValidator.isAmountDivisibleBy10(request.getAmount()))
                .or(() -> RequestValidator.hasSufficientBalance(request.getAmount(), AccountRepository.getBalance(request.getAccountNumber())))
                .map(status -> status.asRuntimeException())
                .ifPresentOrElse(
                        e -> responseObserver.onError(e),
                        () -> sendMoney(request, responseObserver)
                );
    }

    private void sendMoney(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var requestedAmount = request.getAmount();
        IntStream.range(0, requestedAmount / 10)
                .forEach(i -> {
                    var money = Money.newBuilder().setAmount(10).build();
                    responseObserver.onNext(money);
                    log.info("money sent {}", money);
                    AccountRepository.deductAmount(accountNumber, 10);
                    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                });
        responseObserver.onCompleted();
    }
}
