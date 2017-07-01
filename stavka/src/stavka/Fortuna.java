/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stavka;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
//https://www.ifortuna.sk/sk/stavkovanie/futbal?nolimit
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
        
        String url = "https://www.ifortuna.sk/sk/stavkovanie/futbal?limit=100";
        print("Fetching %s...", url);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String title = doc.title();
        Elements links = doc.select("a[href][class]");

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            if (link.attr("class").equals("bet_item_detail_href"))
            {
                text = link.text().trim();
                print(" * a: <%s>  (%s)", link.attr("abs:href"), text);
                teams = link.text().split(" - ");
            }
            if (link.attr("class").matches("add_bet_link betlink-(.*)"))
            {
                print(" * a: <%s>  (%s)", link.attr("abs:href"), link.text().trim());
                rates[i] = Double.valueOf(link.text().trim());
                i++;
            }
            
            if (i == 6) {
                insertRow(teams, rates);
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

/*
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Example program to list links from a URL.
 */
/*public class Fortuna {
    public Fortuna () {
        String url = "http://news.ycombinator.com/";
        print("Fetching %s...", url);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}*/

