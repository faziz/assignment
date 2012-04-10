package org.faziz.assignment.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author faziz
 */
public final class ApplicationUtils {

    private static final Logger logger = Logger.getLogger(ApplicationUtils.class.getName());
    
    /**
     * 
     * @param req
     * @return 
     */
    public static String getPostData(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = req.getReader();
            reader.mark(10000);

            String line;
            do {
                line = reader.readLine();
                sb.append(line).append("\n");
            } while (line != null);
            // do NOT close the reader here, or you won't be able to get the post data twice
            reader.reset();
        } catch (IOException e) {
            // This has happened if the request's reader is closed    
            logger.log(Level.WARNING, "getPostData couldn't.. get the post data", e);
            
        }

        return sb.toString();
    }
}
