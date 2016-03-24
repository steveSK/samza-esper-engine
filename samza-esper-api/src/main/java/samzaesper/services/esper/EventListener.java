/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.services.esper;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author stefan
 */
public class EventListener implements UpdateListener {

    private final Producer<String, Object> producer;
    private final String statementName;
    private final String eventNameKey;
    private final String outputTopic;
    private final EPRuntime runtime;
    private final String partitionKey;
    private final boolean isInsertInto;
    private static final ObjectMapper objMapper = new ObjectMapper();

    public EventListener(Producer producer, EPRuntime runtime, String outputTopic, String statementName, String eventNameKey, String partitionKey, boolean isInsertInto) {
        this.producer = producer;
        this.statementName = statementName;
        this.outputTopic = outputTopic;
        this.eventNameKey = eventNameKey;
        this.isInsertInto = isInsertInto;
        this.runtime = runtime;
        this.partitionKey = partitionKey;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean event : newEvents) {
                Map<String, Object> esperOutput = new HashMap<>();
                if (isInsertInto) {
                    esperOutput = (Map<String, Object>) event.getUnderlying();
                    esperOutput.put(eventNameKey, event.getEventType().getName());

                } else {
                    esperOutput.put("statementname", statementName);
                    esperOutput.put("underlying", event.getUnderlying());
                } 
                Date date = new java.util.Date();
                if (!esperOutput.containsKey("timestamp")) {
                    esperOutput.put("timestamp", runtime.getCurrentTime());
                }
                producer.send(new KeyedMessage<String, Object>(outputTopic, partitionKey, esperOutput));

            }
        }
    }
}
