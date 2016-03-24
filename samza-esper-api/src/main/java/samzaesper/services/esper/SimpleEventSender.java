/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.services.esper;

import com.espertech.esper.client.EPServiceProvider;
import java.util.Map;

/**
 *
 * @author stefan
 */
public class SimpleEventSender implements EventSender{
    
    private final EPServiceProvider esperProvider;
    
    public SimpleEventSender(EPServiceProvider esperProvider){
        this.esperProvider = esperProvider;
    }
    
    @Override
    public void sendEvent(Map<String, Object> event, String eventType) {
        esperProvider.getEPRuntime().sendEvent(event, eventType);
    }
    
}
