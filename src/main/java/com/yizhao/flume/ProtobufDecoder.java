package com.yizhao.flume;

/**
 * Created by yizhao on 6/24/15.
 */

import com.google.protobuf.InvalidProtocolBufferException;
import com.yizhao.proto.KafkaProto.KafkaLoggingMessage;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

public class ProtobufDecoder  implements Decoder<Object> {
    private static final Logger LOGGER = Logger.getLogger(ProtobufDecoder.class);
    public ProtobufDecoder(VerifiableProperties verifiableProperties) {
        /* This constructor must be present for successful compile. */
    }

    @Override
    public Object fromBytes(byte[] bytes) {
        try {
            if (bytes == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("At ProtobufDecoder class, bytes is null");
                }
                return null;
            }
            return KafkaLoggingMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}