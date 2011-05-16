/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.SendMail;

import at.redeye.FrameWork.utilities.base64.Base64Coder;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class SendMail
{
    private static final Logger logger = Logger.getLogger(SendMail.class);
     String mailhost;

    public SendMail( String mailhost )
    {        
        this.mailhost = mailhost;
        logger.info("mailhost is " + mailhost);
        System.getProperties().put("mail.host", mailhost );
    }

    public void sendMail( String to, String from, String subject, String message ) throws MalformedURLException, IOException
    {
        sendMail( to, from, subject, message, false );
    }

    public void sendMail( String to, String from, String subject, String message, boolean html ) throws MalformedURLException, IOException
    {
        URL u = new URL("mailto:" + to );

        URLConnection c = u.openConnection(); // Create a URLConnection for it
        c.setDoInput(false);                  // Specify no input from this URL
        c.setDoOutput(true);                  // Specify we'll do output

        System.out.println("Connecting...");  // Tell the user what's happening
        System.out.flush();                   // Tell them right now
        c.connect();                          // Connect to mail host
        PrintWriter out = // Get output stream to mail host
                new PrintWriter(new OutputStreamWriter(c.getOutputStream(),"US-ASCII"));

        /*
        // Write out mail headers.  Don't let users fake the From address
        out.println("From: \"" + from + "\" <"
                + System.getProperty("user.name") + "@"
                + InetAddress.getLocalHost().getHostName() + ">");         
         */
        
        subject = "=?utf-8?b?" + Base64Coder.encodeString(subject) + "?=";
        
        logger.debug("From: " + from);
        logger.debug("To: " + to);
        logger.debug("Subject: " + subject);
        
        out.println("From: " + from );
        out.println("To: " + to);
        out.println("Subject: " + subject);

        out.println("Content-Transfer-Encoding: base64");
        
        if( html )
            out.println("Content-Type: text/html;\r\n charset=\"utf-8\"\r\n");
        else
            out.println("Content-Type: text/plain;\r\n charset=\"utf-8\"\r\n");        
        
        out.println();  // blank line to end the list of headers

        // Now ask the user to enter the body of the message
         out.println(Base64Coder.encodeString(message));

         out.close();
         logger.debug("Message to " + to + " sent.");         
    }
}
