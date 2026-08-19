package com.alejandro.common;

import com.alejandro.sec06.BankService;
import com.alejandro.sec06.TransferService;

public class Demo {

    static void main() {
        GrpcServer.create(new BankService(), new TransferService())
                .start()
                .await();
    }
}
