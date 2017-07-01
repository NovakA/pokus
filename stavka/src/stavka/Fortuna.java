/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stavka;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author adam
 */
public class Fortuna {
    
    private static String tableName = "FORTUNA";
    private static Connection connection = null;
    private static Statement statement = null;
    private static int counter = 1;
    
    static String dbURL = "jdbc:derby://localhost:1527/stavka;create=true;";
    static String user = "stavka";
    static String password = "stavka";
    
    public Fortuna () {
        
        String[] teams = null;
        double[] rates = new double[6];
        String text;
        int i = 0;
        
        createConnection();

        File input = new File("fortuna.txt");
        Document doc = null;
        
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Elements links = doc.select("td[class]");
        Elements childElement = null;

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            if (link.attr("class").matches("col_bet  col_bet_empty"))
            {
                rates[i] = 0.0;
                i++;
            } else if  ((link.attr("class").equals("col_title")) || (link.attr("class").matches("col_bet(.*)"))){
                childElement = link.select("a");
            } 
            
            if (childElement != null) {
                if (childElement.attr("class").equals("bet_item_detail_href"))
                {
                    text = childElement.text().trim();
                    print(" * a: <%s>  (%s)", childElement.attr("abs:href"), text);
                    teams = childElement.text().split(" - ");
                    i = 0;
                }

                if (childElement.attr("class").matches("add_bet_link betlink-(.*)"))
                {
                    print(" * a: <%s>  (%s)", childElement.attr("abs:href"), childElement.text().trim());
                    rates[i] = Double.valueOf(childElement.text().trim());
                    i++;
                }
                childElement = null;
            }
            
            if (i == 6) {
                if (teams.length == 2) {
                    insertRow(teams, rates);
                }
                i = 0;
                teams = null;
            }
        }
        
        shutdown();
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
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
        }
    }
    
    /**
     * Ulozenie do databazy.
     * @param currency mena, ktora sa ma vlozit resp. kontrolovat
     * @param rates kurzy, ktore sa vkladaju do DB
     */
    private static void insertRow(String[] teams, double[] rates) {
        
        try
        {
            statement = connection.createStatement();
            
            statement.execute("insert into " + tableName + " values (" + counter + ",'" + 
                    teams[0] + "','" + teams[1] + "'," + rates[0] + "," + rates[1] + "," + rates[2] +
                    "," + rates[3] + "," + rates[4] + "," + rates[5] + ")");
            
            statement.close();
        }
        catch (SQLException sqlExcept)
        {
        }
        
        counter++;
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