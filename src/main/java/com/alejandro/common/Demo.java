package com.alejandro.common;

import com.alejandro.sec06.TransferService;
import com.alejandro.sec07.FlowControlService;
import com.alejandro.sec10.BankService;

public class Demo {

    static void main() {
        GrpcServer.create(new BankService())
                .start()
                .await();
    }
}
