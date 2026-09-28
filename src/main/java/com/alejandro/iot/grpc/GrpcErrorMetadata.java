package com.alejandro.iot.grpc;

import com.alejandro.iot.models.ErrorMessage;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;

public final class GrpcErrorMetadata {

    public static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

    private GrpcErrorMetadata() {

    }
}
