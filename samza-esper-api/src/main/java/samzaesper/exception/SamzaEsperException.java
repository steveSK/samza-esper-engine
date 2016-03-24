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
public class SamzaEsperException extends Exception{
    
    public SamzaEsperException() { super(); }
    public SamzaEsperException(String message) { super(message); }
    public SamzaEsperException(String message, Throwable cause) {super(message,cause); } 
    public SamzaEsperException(Throwable cause) { super(cause); }
    
}
