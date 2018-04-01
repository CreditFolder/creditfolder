package io.creditfolder.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 10:44
 */
public class IPUtil {

    public static String getExternalIp() {
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        }
        catch (IOException e) {
            return null;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                }
            }
        }
    }

    public static void main(String args[]) {
        String ip = IPUtil.getExternalIp();
        System.out.println(ip);
    }
}
