package com.alejandro.common;

import com.alejandro.sec06.BankService;

public class Demo {

    static void main() {
        GrpcServer.create(new BankService())
                .start()
                .await();
    }
}
