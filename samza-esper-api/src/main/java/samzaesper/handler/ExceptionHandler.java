/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.handler;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author stefan
 */
public class ExceptionHandler {

    private static final String EXC_TOPIC = "exception-output";
    private static final Log log = LogFactory.getLog(ExceptionHandler.class);

    public void handleException(Exception e, Producer<String, Object> producer) {
        producer.send(new KeyedMessage<String, Object>(EXC_TOPIC, e.getMessage()));
        log.error(e);
    }

}
