package com.alejandro.sec03;

import com.alejandro.models.sec03.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01Scalar {

    private static final Logger log = LoggerFactory.getLogger(Lec01Scalar.class);

    static void main() {

        var person = Person.newBuilder()
                .setLastName("sam")
                .setAge(12)
                .setEmail("sam@gmail.com")
                .setEmployed(true)
                .setSalary(1000.2345)
                .setBankAccountNumber(12345678901L)
                .setBalance(-1000)
                .build();

        log.info("{}" ,person);

    }
}
