/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.services.esper;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.CurrentTimeSpanEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefan
 */
public class ExternalTimestampEventSender implements EventSender {

    private final EPServiceProvider esperProvider;
    private final String initialTimeString;
    private final String externalTimestampField;
    private final SimpleDateFormat dateFormat;
    private boolean timeHasStarted = false;

    public ExternalTimestampEventSender(EPServiceProvider esperProvider, String initialTimeString, String externalTimestampField, String dateFormat) {
        this.esperProvider = esperProvider;
        this.initialTimeString = initialTimeString;
        this.externalTimestampField = externalTimestampField;
        this.dateFormat = new SimpleDateFormat(correctDateFormat(dateFormat));
    }

    @Override
    public void sendEvent(Map<String, Object> event, String eventType) {
        Object timestampObject = event.get(externalTimestampField);
        long timestamp;
        try {
            if (timestampObject instanceof Long) {
                timestamp = (long) timestampObject;
            } else {
                timestamp = dateFormat.parse((String) event.get(externalTimestampField)).getTime();
            }
            if (timeHasStarted) {
                esperProvider.getEPRuntime().sendEvent(event, eventType);
                esperProvider.getEPRuntime().sendEvent(new CurrentTimeSpanEvent(timestamp + 1));
            } else {

                Date initialTime = dateFormat.parse(initialTimeString);
                esperProvider.getEPRuntime().sendEvent(new CurrentTimeEvent(initialTime.getTime()));
                esperProvider.getEPRuntime().sendEvent(event, eventType);
                esperProvider.getEPRuntime().sendEvent(new CurrentTimeSpanEvent(timestamp + 1));
                timeHasStarted = true;
            }
        } catch (ParseException ex) {
            Logger.getLogger(ExternalTimestampEventSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String correctDateFormat(String dateFormat) {
        if (dateFormat.contains("T")) {
            int index = dateFormat.indexOf("T");
            return dateFormat.substring(0, index) + "'T'" + dateFormat.substring(index + 1, dateFormat.length());
        }
        return dateFormat;
    }

}
