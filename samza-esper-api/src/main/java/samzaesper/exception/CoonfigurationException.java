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
public class CoonfigurationException extends SamzaEsperException{
    
    public CoonfigurationException() { super(); }
    public CoonfigurationException(String message) { super(message); }
    public CoonfigurationException(String message, Throwable cause) {super(message,cause); } 
    public CoonfigurationException(Throwable cause) { super(cause); }
    
    
}
