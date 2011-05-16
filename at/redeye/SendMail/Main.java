/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.SendMail;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.BaseAppConfigDefinitions;
import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.utilities.ReadFile;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.base64.Base64Coder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.log4j.Level;

/**
 *
 * @author martin
 */
public class Main extends BaseModuleLauncher
{    
    public Main( String args[] )
    {
       super(args);              
        
        if (StringUtils.isYes(getStartupParam("debug"))) {
            BaseConfigureLogging(Level.ALL);
        } else {
            BaseConfigureLogging(Level.ERROR);
        }
                  
       root = new LocalRoot("SendMail");        
       
       String logging_dir = getStartupParam("logdir");
       
       if( logging_dir != null ) {
           BaseAppConfigDefinitions.LoggingDir.setConfigValue(logging_dir);
           BaseAppConfigDefinitions.LoggingLevel.setConfigValue("ALL");
           BaseAppConfigDefinitions.DoLogging.setConfigValue("true");                            
       } else {
           BaseAppConfigDefinitions.DoLogging.setConfigValue("false" );
           BaseAppConfigDefinitions.LoggingLevel.setConfigValue("ERROR");           
       }             
    }
       
    @Override
    public String getVersion() {
        return "0.1";
    }
    
    public void run() throws Exception
    {
        configureLogging();
        
        Locale loc = Locale.getDefault();
        
        logger.trace("Locale: " + Locale.getDefault().toString() + " Charset " + Charset.defaultCharset().displayName());        
                
        String mail_from    = getStartupParam("from");
        String mail_to      = getStartupParam("to");
        String mail_from64    = getStartupParam("from64");
        String mail_to64      = getStartupParam("to64");                
        String mail_subject = getStartupParam("subject");
        String mail_host    = getStartupParam("mailhost");
        String mail_subject64 = getStartupParam("subject64");
        String mail_file = getStartupParam("file");
        
        if( mail_subject == null && mail_subject64 != null )
        {
            mail_subject = new String(Base64Coder.decode(mail_subject64),"UTF-8");
            logger.trace("subject: " + mail_subject);
        }                

        if( mail_from == null && mail_from64 != null )
        {
            mail_from = new String(Base64Coder.decode(mail_from64),"UTF-8");
            logger.trace("from: " + mail_from);
        }                
        
        if( mail_to == null && mail_to64 != null )
        {
            mail_to = new String(Base64Coder.decode(mail_to64),"UTF-8");
            logger.trace("to: " + mail_to);
        }                        
        
        boolean is_html = StringUtils.isYes(getStartupParam("html"));
        
        if( mail_from == null ||
            mail_to == null   ||
            mail_subject == null ||
            mail_host == null )
        {
            usage();
        }               
        
        String mail_message = null;
        
        if( mail_file != null )
        {
            mail_message = ReadFile.read_file_string(mail_file);
            
            if( mail_message == null || mail_message.isEmpty() ) 
            {
                throw new Exception( "Cannot read message file");
            }
        } else {
            /** read from stdin */
            System.out.println("Please enter you message now. Press STRG+D when finnisched. Or one line with sign\n");
            System.out.flush();
            
            BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
                    
            StringBuilder msg = new StringBuilder();
            
            String str = "";
            
            while( str != null ) 
            {
                str = in.readLine();
                
                if( str != null )
                {
                    msg.append(str);
                    msg.append('\n');
                }
            }
            
            mail_message = msg.toString();
        }
        
        if( mail_message == null )
        {
            throw new Exception( "No mail message" );
        }                               
        
        SendMail send_mail = new SendMail(mail_host);
        send_mail.sendMail(mail_to, mail_from, mail_subject, mail_message, is_html);
    }
    
    private void usage()
    {
        System.out.println("Usage: java -jar SendMail -mailhost mail.host -from from@address -to to@address -subject \"bla bla\" [-file filename] [-html (true|false|0|1)]\n" +
                "\tIf -file is not specified, the message is expected from stdin\n" +
                "\t-html indecates that the message is a html message\n");
        System.exit(1);
    }
    
    public static void main( final String args[] )
    {
        AutoLogger al = new AutoLogger( Main.class.getName() )
        {
            @Override
            public void do_stuff() throws Exception {
                Main main = new Main(args);
        
                main.run();
            }            
        };                    
        
        if( al.isFailed() ) {            
            System.exit(1);
        }
        
        System.exit(0);
    } 
}
