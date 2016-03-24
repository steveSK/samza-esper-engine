/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.services.esper;

import samzaesper.statement.EsperStatement;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventType;

import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kafka.javaapi.producer.Producer;
import samzaesper.utils.SamzaesperUtils;

/**
 *
 * @author stefan
 */
public class EsperService {

    private final EPServiceProvider esperProvider;
    private int mapEventCount;
    private final EventSender eventSender;
    private static final String MAP_EVENT_NAME = "MapEvent"; 


    public  EsperService(URL esperConfUrl, int taskID, String initialTimeString, String externalTimestampField, String externalTimestampFormat) throws EPException {
        mapEventCount = 0;
        Configuration configuration = new Configuration();
        configuration.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        configuration.configure(esperConfUrl);
        esperProvider = EPServiceProviderManager.getProvider(String.valueOf(taskID), configuration);
        eventSender = new ExternalTimestampEventSender(esperProvider,initialTimeString,externalTimestampField,externalTimestampFormat);

    }
    
    public EsperService(URL esperConfUrl, int taskID){
        mapEventCount = 0;
        Configuration configuration = new Configuration();
        configuration.configure(esperConfUrl);
        esperProvider = EPServiceProviderManager.getProvider(String.valueOf(taskID),configuration);
        eventSender = new SimpleEventSender(esperProvider);
        
    }

    public int getMapEventCount() {
        return mapEventCount;
    }
    
    public void sendEvent(Map<String, Object> event, String eventType) throws EPException, ParseException {
           eventSender.sendEvent(event, eventType);
    }

    public boolean isEventRegistered(String eventName) {
        return esperProvider.getEPAdministrator().getConfiguration().isEventTypeExists(eventName);
    }

    public void registerEventType(String eventTypeName, Map<String, Object> eventTypeMap) {
        esperProvider.getEPAdministrator().getConfiguration().addEventType(eventTypeName, eventTypeMap);
    }

    public void registerEventTypeFromUnknowEvent(String evenTypeName, Map<String, Object> jsonObject) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value != null && SamzaesperUtils.isMap(value)) {
                eventTypeMap.put(key, (Map) value);
                registerEventTypeFromUnknowEvent(null, (Map) value);
            } else if (value != null && SamzaesperUtils.isArray(value)) {
                List<Object> array = (List<Object>) value;
                if (SamzaesperUtils.isMap(array.get(0))) {
                    registerEventTypeFromUnknowEvent(MAP_EVENT_NAME + getMapEventCount(), (Map) array.get(0));
                    eventTypeMap.put(key, MAP_EVENT_NAME + getMapEventCount() + "[]");
                    mapEventCount++;
                } else {
                    eventTypeMap.put(key, Object[].class);
                }
            } else {
                eventTypeMap.put(key, Object.class);
            }
        }
        if (evenTypeName != null) {
            registerEventType(evenTypeName, eventTypeMap);
        }
    }

    public void createEPLWithListener(String statementName, String eplStatement, boolean isPattern, Producer producer, String outputTopic, String partitionKey, String eventNameKey) throws EPException {
        createEPLWithListener(new EsperStatement(statementName, eplStatement, isPattern, producer, outputTopic, partitionKey, eventNameKey));
    }
    
    public void registerStatements(List<EsperStatement> statements){
        for(EsperStatement statement : statements){
            createEPLWithListener(statement);
        }
    }

    private EPStatement createEPLWithListener(EsperStatement esperStatement) throws EPException {
        EPStatement stmt;
        if (esperStatement.isPattern()) {
            stmt = esperProvider.getEPAdministrator().createPattern(esperStatement.getStatementDefinition(), esperStatement.getStatementName());
            stmt.addListener(new EventListener(esperStatement.getProducer(), esperProvider.getEPRuntime(), esperStatement.getOutputTopic(), esperStatement.getStatementName(),esperStatement.getEventNameKey(), esperStatement.getPartitionKey(), false));
        } else {
            stmt = esperProvider.getEPAdministrator().createEPL(esperStatement.getStatementDefinition(), esperStatement.getStatementName());
            stmt.addListener(new EventListener(esperStatement.getProducer(), esperProvider.getEPRuntime(), esperStatement.getOutputTopic(), esperStatement.getStatementName(), esperStatement.getEventNameKey(), esperStatement.getPartitionKey(), isInserStream(esperStatement.getStatementDefinition())));
        }
        return stmt;
    }

    public void destroyEPLStatement(String statementName) {
        EPStatement stmt = esperProvider.getEPAdministrator().getStatement(statementName);
        if (stmt != null) {
            stmt.destroy();
        }
    }

    public void destroyAllStatements() {
        esperProvider.getEPAdministrator().destroyAllStatements();
    }

    public EventType[] getListOfEventTypes() {
        return esperProvider.getEPAdministrator().getConfiguration().getEventTypes();
    }

    public Map<String, String> getListOfStatements() {
        Map<String, String> statements = new HashMap<>();

        String[] statementNames = esperProvider.getEPAdministrator().getStatementNames();
        for (String statement : statementNames) {
            statements.put(statement, esperProvider.getEPAdministrator().getStatement(statement).getText());
        }
        return statements;
    }

    private boolean isInserStream(String eplStatement) {
        return eplStatement.toLowerCase().contains("insert into");
    }

}
