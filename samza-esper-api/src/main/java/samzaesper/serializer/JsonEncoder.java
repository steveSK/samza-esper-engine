/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.serializer;

import java.io.IOException;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author stefan
 */
public class JsonEncoder implements Encoder<Object> {

    private static final Log LOGGER = LogFactory.getLog(JsonEncoder.class);

    public JsonEncoder(VerifiableProperties verifiableProperties) {
        /* This constructor must be present for successful compile. */
    }

    @Override
    public byte[] toBytes(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object).getBytes();
        } catch (IOException e) {
            LOGGER.error(String.format("Json processing failed for object: %s", object.getClass().getName()), e);
        }
        return "".getBytes();
    }
}
