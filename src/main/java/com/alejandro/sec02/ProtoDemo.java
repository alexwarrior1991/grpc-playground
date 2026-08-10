package com.alejandro.sec02;

import com.alejandro.models.sec02.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoDemo {

    public static final Logger log = LoggerFactory.getLogger(ProtoDemo.class);

    static void main() {

        var person = Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .build();

        log.info("{}", person);

    }
}
