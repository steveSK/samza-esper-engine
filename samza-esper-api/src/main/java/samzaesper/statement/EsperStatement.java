/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.statement;

import kafka.javaapi.producer.Producer;

/**
 *
 * @author stefan
 */
public class EsperStatement {
    
    private final String statementName;
    private final String statementDefinition;
    private final boolean isPattern;
    private final Producer producer;
    private final String outputTopic;
    private final String partitionKey;
    private final String eventNameKey;

    public EsperStatement(String statementName, String statementDefinition, boolean isPattern, Producer producer, String outputTopic,String partitionKey, String eventNameKey) {
        this.statementName = statementName;
        this.statementDefinition = statementDefinition;
        this.isPattern = isPattern;
        this.producer = producer;
        this.outputTopic = outputTopic;
        this.eventNameKey = eventNameKey;
        this.partitionKey = partitionKey;
    }

    public String getStatementName() {
        return statementName;
    }

    public String getStatementDefinition() {
        return statementDefinition;
    }

    public boolean isPattern() {
        return isPattern;
    }

    public Producer getProducer() {
        return producer;
    }

    public String getOutputTopic() {
        return outputTopic;
    }
    
    public String getPartitionKey(){
        return partitionKey;
    }

    public String getEventNameKey() {
        return eventNameKey;
    }
    
}
