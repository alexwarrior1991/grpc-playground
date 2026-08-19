package com.alejandro.common;

import com.alejandro.sec06.BankService;
import com.alejandro.sec06.TransferService;
import com.alejandro.sec07.FlowControlService;

public class Demo {

    static void main() {
        GrpcServer.create(new FlowControlService())
                .start()
                .await();
    }
}
