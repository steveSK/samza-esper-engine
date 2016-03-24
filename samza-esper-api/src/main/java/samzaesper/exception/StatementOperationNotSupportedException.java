/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.exception;

/**
 *
 * @author stefan
 */
public class StatementOperationNotSupportedException extends SamzaEsperException {
    public StatementOperationNotSupportedException() { super(); }
    public StatementOperationNotSupportedException(String message) { super(message); }
    public StatementOperationNotSupportedException(String message, Throwable cause) {super(message,cause); } 
    public StatementOperationNotSupportedException(Throwable cause) { super(cause); }
    
}
