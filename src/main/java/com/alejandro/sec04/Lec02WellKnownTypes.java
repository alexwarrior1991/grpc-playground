package com.alejandro.sec04;

import com.alejandro.models.sec04.Sample;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class Lec02WellKnownTypes {

    private static final Logger log = LoggerFactory.getLogger(Lec02WellKnownTypes.class);

    static void main() {

        var sample = Sample.newBuilder()
                .setAge(Int32Value.of(12))
                .setLoginTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build();

        log.info("{}", Instant.ofEpochSecond(sample.getLoginTime().getSeconds()));
    }
}
