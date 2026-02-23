package kafka.serializer;

import com.google.protobuf.MessageLite;
import org.apache.kafka.common.serialization.Serializer;

public class ProtobufSerializer implements Serializer<MessageLite> {

    @Override
    public byte[] serialize(String topic, MessageLite data) {
        if (data == null) return null;
        return data.toByteArray();
    }
}
