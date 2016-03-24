/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.parser;

import java.util.Arrays;
import samzaesper.statement.StatementOperation;
import samzaesper.statement.StatementOperationType;
import java.util.List;
import java.util.Map;
import samzaesper.exception.StatementOperationNotSupportedException;
import samzaesper.utils.SamzaesperUtils;

/**
 *
 * @author stefan
 */
public class JsonParser implements Parser {

    /**
     * make nested properties accessible for esper engine, convert list to array
     *
     * @param jsonMessage
     * @return
     */
    @Override
    public Map<String, Object> parseEventMessage(Map<String, Object> jsonMessage) {
        for (String key : jsonMessage.keySet()) {
            Object value = jsonMessage.get(key);
            if (value != null && value instanceof Object[]) {
                List<Object> arrayList =  Arrays.asList(value);
                jsonMessage.put(key, arrayList.toArray());
                for (Object item : arrayList) {
                    if (SamzaesperUtils.isMap(item)) {
                        parseEventMessage((Map<String, Object>) item);
                    }
                }
            }

        }
        return jsonMessage;
    }

    /*    @Override
     public Map<String, Object> createEventType(Map<String, Object> jsonObject, EsperService esperService) {
     Map<String, Object> eventTypeMap = new HashMap<String, Object>();
     for (String key : jsonObject.keySet()) {
     Object value = jsonObject.get(key);
     if (value != null && isMap(value)) {
     eventTypeMap.put(key, createEventType((Map) value, esperService));
     } else if (value != null && isArray(value)) {
     List<Object> array = (List<Object>) value;
     if (isMap(array.get(0))) {
     Map<String, Object> arrayMapProperty = createEventType((Map) array.get(0), esperService);
     esperService.registerEvent("MapEvent", arrayMapProperty, true);
     eventTypeMap.put(key, "MapEvent" + esperService.getMapEventCount() + "[]");
     } else {
     eventTypeMap.put(key, Object[].class);
     }
     } else {
     eventTypeMap.put(key, Object.class);

     }
     }
     return eventTypeMap;
     } */
    @Override
    public StatementOperation parseStatementOperation(Map<String, Object> jsonMessage) throws StatementOperationNotSupportedException {

        StatementOperationType opType;
        try {
            opType = StatementOperationType.valueOf(jsonMessage.get("operation").toString());
        } catch (IllegalArgumentException e) {
            throw new StatementOperationNotSupportedException("operation "
                    + jsonMessage.get("operation").toString() + " is not supported");
        }
        String statementName = jsonMessage.get("name") == null ? null : jsonMessage.get("name").toString();
        String statementDefinition = jsonMessage.get("definition") == null ? null : jsonMessage.get("definition").toString();
        boolean isPattern = jsonMessage.get("isPattern") == null ? false : Boolean.valueOf(jsonMessage.get("isPattern").toString());

        return new StatementOperation(opType, statementName, statementDefinition, isPattern);

    }

}
