package com.alejandro.sec05;

import com.alejandro.models.sec05.v3.Television;
import com.alejandro.models.sec05.v3.Type;
import com.alejandro.sec05.parser.V1Parser;
import com.alejandro.sec05.parser.V2Parser;
import com.alejandro.sec05.parser.V3Parser;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class V3VersionCompatibility {

    private static final Logger log = LoggerFactory.getLogger(V3VersionCompatibility.class);

    static void main() throws InvalidProtocolBufferException {

        var tv = Television.newBuilder()
                .setBrand("samsung")
                .setType(Type.UHD)
                .build();

        V1Parser.parse(tv.toByteArray());
        V2Parser.parse(tv.toByteArray());
        V3Parser.parse(tv.toByteArray());
    }
}
