package com.yizhao.flume;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import com.yizhao.proto.KafkaProto.KafkaLoggingMessage;
import com.yizhao.proto.TxnPayloadFriendProto.TxnPayloadFriend;
import com.yizhao.proto.TxnResponseFriendProto.TxnResponseFriend;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Created by yizhao on 6/18/15.
 */
// bin/flume-ng agent --conf conf --conf-file conf/custominterceptor.conf -name a1 -Dflume.root.logger=INFO,console
// cp /home/yi.zhao/CustomFlumeInterceptorsProtocalBuffer-1.0-SNAPSHOT.jar /fuhu/flumeuser/flume/lib
public class CustomInterceptor
        implements Interceptor {

    private String moreBody;
    private static ExtensionRegistry registry;


    static {
        registry = ExtensionRegistry.newInstance();
        registry.add(TxnPayloadFriend.payload);
        registry.add(TxnResponseFriend.response);
    }


    public CustomInterceptor(String moreBody) {
        this.moreBody = moreBody;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {

        // This is the event's body
        KafkaLoggingMessage decodeMessage = null;

        try {
            decodeMessage = KafkaLoggingMessage.parseFrom(event.getBody(), registry);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        String textFormat = TextFormat.printToString(decodeMessage);
        System.out.println("ApikeyOpId:" + decodeMessage.getApikeyOpId());
        System.out.println("textFormat:" + textFormat);
        // These are the event's headers
        Map<String, String> headers = event.getHeaders();


        String newBody = moreBody ;
        event.setBody(textFormat.getBytes());


        // Let the enriched event go
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {

        List<Event> interceptedEvents =
                new ArrayList<Event>(events.size());
        for (Event event : events) {
            // Intercept any event
            Event interceptedEvent = intercept(event);
            interceptedEvents.add(interceptedEvent);
        }

        return interceptedEvents;
    }

    @Override
    public void close() {
        // At interceptor shutdown
    }

    public static class Builder
            implements Interceptor.Builder {

        private String moreBody;

        @Override
        public void configure(Context context) {
            // Retrieve property from flume conf
            moreBody = context.getString("moreBody");
        }

        @Override
        public Interceptor build() {
            return new CustomInterceptor(moreBody);
        }
    }
}