package com.alejandro.sec02;

import com.alejandro.models.sec02.Person;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoDemo {

    private static final Logger log = LoggerFactory.getLogger(ProtoDemo.class);

    static void main() {

        var person1 = createPerson();

        var person2 = createPerson();

        // compare
        log.info("equals {}", person1.equals(person2));
        log.info("== {}", (person1 == person2));

        // mutable? No

        // create another instance with diff value
        var person3 = person1.toBuilder().setName("mike").build();

        // compare
        log.info("equals {}", person1.equals(person3));
        log.info("== {}", (person1 == person3));


        // null?
        var person4 = person1.toBuilder().clearName().build();
        log.info("person4: {}", person4);
    }

    private static @NonNull Person createPerson() {
        return Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .build();
    }
}
