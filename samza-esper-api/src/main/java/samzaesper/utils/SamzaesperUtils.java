/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samzaesper.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author stefan
 */
public class SamzaesperUtils {

    public static boolean isValidProtocol(String url) {
        if ("http://".equals(url.substring(0, 7))) {
            return true;
        }
        if ("https://".equals(url.substring(0, 8))) {
            return true;
        }
        if ("file://".equals(url.substring(0, 7))) {
            return true;
        }
        return "ftp://".equals(url.substring(0, 6));
    }

    public static URL formatURL(String unformatedURL) throws MalformedURLException {

        if (isValidProtocol(unformatedURL)) {
            return new URL(unformatedURL);
        }
        if (unformatedURL.charAt(0) == '/') {
            return new URL("file://" + unformatedURL);
        } else {
            return new URL("file://" + System.getProperty("user.dir") + "/__package/" + unformatedURL);
        }

    }

    public static Boolean isMap(Object obj) {
        return obj.toString().charAt(0) == '{';

    }

    public static Boolean isArray(Object obj) {
        return obj.toString().charAt(0) == '[';
    }

}
