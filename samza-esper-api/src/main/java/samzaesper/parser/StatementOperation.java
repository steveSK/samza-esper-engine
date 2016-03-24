/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.parser;

/**
 *
 * @author stefan
 */

public class StatementOperation {
    
    private final StatementOperationType statementOperationType;
    private final String statementName;
    private final String statementDefinition;
    private final boolean isPattern;

    public StatementOperation(StatementOperationType statementOperationType, String statementName, String statementDefinition, boolean isPattern) {
        this.statementOperationType = statementOperationType;
        this.statementName = statementName;
        this.statementDefinition = statementDefinition;
        this.isPattern = isPattern;
    }

    public StatementOperationType getStatementOperationType() {
        return statementOperationType;
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
    
    
    
    
    
    
}
