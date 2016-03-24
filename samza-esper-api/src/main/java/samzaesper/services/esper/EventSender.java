/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.services.esper;

import java.util.Map;

/**
 *
 * @author stefan
 */
public interface EventSender {
        
    void sendEvent(Map<String, Object> event, String eventType);
    
}
