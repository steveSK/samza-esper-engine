/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.udf;

import com.maxmind.geoip.LookupService;
import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author stefan
 */
public class GeoIPLookup {
    
   private static final String dbfile = "/tmp/GeoIP.dat";
   private static final LookupService cl = createLookupService(dbfile, LookupService.GEOIP_MEMORY_CACHE);
    
    
    public static String getCountry(Object IP) 
    {
    
        if(cl == null || !(IP instanceof String))
        {
           return "db not find";
        }
       return  cl.getCountry(String.valueOf(IP)).getCode();
   
        
    }
    
    private static LookupService createLookupService(String dbfile, int options) 
    {
        try {
            return new LookupService(dbfile,options);
        } catch (IOException ex) {
            LogFactory.getLog(GeoIPLookup.class.getName()).error(ex);
        }
        return null;
    }
    
}
