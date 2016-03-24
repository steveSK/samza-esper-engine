/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.parser;

import samzaesper.statement.StatementOperation;
import java.util.Map;
import samzaesper.exception.StatementOperationNotSupportedException;

/**
 *
 * @author stefan
 */
public interface Parser {

    public Map<String, Object> parseEventMessage(Map<String, Object> jsonMessage);

    public StatementOperation parseStatementOperation(Map<String, Object> jsonMessage) throws StatementOperationNotSupportedException;

}
