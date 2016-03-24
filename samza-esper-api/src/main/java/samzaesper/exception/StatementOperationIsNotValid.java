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
public class StatementOperationIsNotValid extends SamzaEsperException{
    
    public StatementOperationIsNotValid() { super(); }
    public StatementOperationIsNotValid(String message) { super(message); }
    public StatementOperationIsNotValid(String message, Throwable cause) {super(message,cause); } 
    public StatementOperationIsNotValid(Throwable cause) { super(cause); }
    
}
