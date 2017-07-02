/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stavka;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
public class Stavka {
        
    static String dbURL = "jdbc:derby://localhost:1527/stavka;create=true;";
    static String user = "stavka";
    static String password = "stavka";
    
    public static Connection connection = null;
    public static Statement statement = null;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        /*URL website = new URL("https://www.ifortuna.sk/sk/stavkovanie/futbal?nolimit");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("fortuna.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/

        
        /*website = new URL("https://tipkurz.etipos.sk/Odds.aspx?g=0&i=14888&v=0");
        rbc = Channels.newChannel(website.openStream());
        fos = new FileOutputStream("tipos.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/
        
        createConnection();
        
        //Fortuna fortuna = new Fortuna();
        //Tipos tipos = new Tipos();
        Nike nike = new Nike();
        
        shutdown();
    }
    
    /**
     * Vytvori spojenie s databazou.
     */
    private static void createConnection()
    {
        System.out.println("Pripajanie na databazu...");
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(dbURL, user, password);  
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException except)
        {
            Logger.getLogger(Stavka.class.getName()).log(Level.SEVERE, null, except);
        }
    }
    
    
    /**
     * Odhlasuje od databazy. Zatvara spojenie.
     */
    private static void shutdown() {
        
        System.out.println("Odhlasovanie z databazy...");
        try
        {
            if (statement != null)
            {
                statement.close();
            }
            if (connection != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                connection.close();
            }           
        }
        catch (SQLException sqlExcept){}
    }
}
