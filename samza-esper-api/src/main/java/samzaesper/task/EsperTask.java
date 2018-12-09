/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.task;

import com.espertech.esper.client.EPException;

import java.net.URL;
import java.util.Map;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import java.util.Map.Entry;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samzaesper.exception.StatementOperationIsNotValid;
import samzaesper.handler.ExceptionHandler;
import samzaesper.parser.JsonParser;
import samzaesper.parser.Parser;
import samzaesper.parser.StatementConfigurationParser;
import samzaesper.statement.StatementOperation;
import samzaesper.services.esper.EsperService;
import samzaesper.utils.SamzaesperUtils;

/**
 *
 * @author stefan
 */
public class EsperTask implements StreamTask, InitableTask {

    private EsperService esperService;
    private String eventNameKey;
    private ExceptionHandler excHandler;
    private Parser parser;
    private String outputTopic;
    private final String administrationTopic = "administration";
    private String uniformNameEvent;
    private boolean isUniform = false;
    private boolean isExternalTimestamp;
    private Producer<String, Object> producer;
    private static final SystemStream DEBUG = new SystemStream("kafka", "debug");
    private static final Log log = LogFactory.getLog(EsperTask.class);
    private int taskID;

    @Override
    public void init(Config config, TaskContext tc) throws Exception {

        log.info("initializing");
        URL esperConfigurationURL = SamzaesperUtils.formatURL(config.get("samzaesper.esper.configuration.events", ""));
        URL statementsConfigurationURL = SamzaesperUtils.formatURL(config.get("samzaesper.esper.configuration.statements", ""));
        Properties producerProperties = new Properties();
        for (Entry<String, String> property : config.subset("systems.kafka.producer.").entrySet()) {
            producerProperties.put(property.getKey(), property.getValue());
        }
        producer = new Producer<>(new ProducerConfig(producerProperties));
        taskID = tc.getPartition().getPartitionId();
        outputTopic = config.get("samzaesper.output");
        eventNameKey = config.get("samzaesper.event.name.key", "");
        uniformNameEvent = config.get("samzaesper.uniform.event.name", "");

        String externalTimestampField = config.get("external_timestamp_field");
        String initialTimeString = config.get("initial.time");
        String externalTimestampFormat = config.get("samzaesper.external.timestamp.timeformat");

        if (!uniformNameEvent.equals("")) {
            isUniform = true;
        }
        log.info("event name key=" + eventNameKey);
        excHandler = new ExceptionHandler();
        parser = new JsonParser();
        try {
            if (externalTimestampField != null && externalTimestampFormat != null && initialTimeString != null) {
                esperService = new EsperService(esperConfigurationURL, taskID, initialTimeString, externalTimestampField, externalTimestampFormat);
            } else {
                esperService = new EsperService(esperConfigurationURL, taskID);
            }

            if (!statementsConfigurationURL.toString().equals("")) {
                esperService.registerStatements(StatementConfigurationParser.parseStatements(statementsConfigurationURL, producer,outputTopic, eventNameKey));
            }
        } catch (EPException e) {
            excHandler.handleException(e, producer);
        }

    }

    @Override
    public void process(IncomingMessageEnvelope ime, MessageCollector mc, TaskCoordinator tc) {
        try {
            Map<String, Object> jsonMessage = (Map<String, Object>) ime.getMessage();
            if ("statements".equals(ime.getSystemStreamPartition().getSystemStream().getStream())) // here later put topic from config
            {
                StatementOperation so = parser.parseStatementOperation(jsonMessage);
                executeStatementOperation(so);
            } else {
                String eventName;
                if (!isUniform) {
                    eventName = (String) jsonMessage.get(eventNameKey);
                    jsonMessage.remove(eventNameKey);
                } else {
                    eventName = uniformNameEvent;
                }
                esperService.sendEvent(parser.parseEventMessage(jsonMessage), eventName);
            }
        } catch (Exception e) {
            excHandler.handleException(e, producer);
        }
    }

    private void executeStatementOperation(StatementOperation stmtOperation) throws StatementOperationIsNotValid {
        if(!isStatementOperationValid(stmtOperation)){
            throw new StatementOperationIsNotValid("Statement Operation is not valid");
        }
        switch (stmtOperation.getStatementOperationType()) {
            case CREATE:
                esperService.createEPLWithListener(stmtOperation.getStatementName(), stmtOperation.getStatementDefinition(),
                        stmtOperation.isPattern(), producer, outputTopic, null, eventNameKey);
                break;
            case DESTROY:
                esperService.destroyEPLStatement(stmtOperation.getStatementName());
                break;
            case DESTROYALL:
                esperService.destroyAllStatements();
                break;
            case GETSTATEMENTS:
                Map<String, String> statements = esperService.getListOfStatements();
                producer.send(new KeyedMessage<String, Object>(administrationTopic, statements));
                break;
        }
    }

    private boolean isStatementOperationValid(StatementOperation stmtOperation) throws StatementOperationIsNotValid {
        return stmtOperation!=null
                && stmtOperation.getStatementDefinition()!= null
                && stmtOperation.getStatementOperationType()!=null
                && stmtOperation.getStatementName()!= null;
    }
}
