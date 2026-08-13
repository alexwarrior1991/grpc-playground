package com.alejandro.sec04;

import com.alejandro.models.common.Address;
import com.alejandro.models.common.BodyStyle;
import com.alejandro.models.common.Car;
import com.alejandro.models.sec04.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01Import {

    private static final Logger log = LoggerFactory.getLogger(Lec01Import.class);

    static void main() {

        var address = Address.newBuilder().setCity("atlanta").build();
        var car = Car.newBuilder().setBodyStyle(BodyStyle.COUPE).build();
        var person = Person.newBuilder()
                .setName("sam")
                .setAge(12)
                .setCar(car)
                .setAddress(address)
                .build();

        log.info("{}", person);
        log.info("{}", person.hasAge());


    }
}
