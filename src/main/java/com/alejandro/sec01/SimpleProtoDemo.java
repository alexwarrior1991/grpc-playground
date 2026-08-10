package com.alejandro.sec01;

import com.alejandro.models.sec01.PersonOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtoDemo {

    public static final Logger log = LoggerFactory.getLogger(SimpleProtoDemo.class);

    static void main() {
        var person = PersonOuterClass.Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .build();

        log.info("{}", person);
    }
}
