/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stavka;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static stavka.Stavka.connection;
import static stavka.Stavka.statement;

/**
 *
 * @author adam
 */
public class Tipos {
    
    private static final String tableName = "TIPOS";
    private static int counter = 1;
    
    public Tipos () {
        
        String[] teams = null;
        double[] rates = new double[6];
        String text;
        int i = 0;
 
        File input = new File("tipos.txt");
        Document doc = null;
        
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Elements links = doc.select("tr[class]");
        Elements childElements = null;

        System.out.print("Links: " + links.size());
        for (Element link : links) {
            childElements = link.select("td");
            
            for (Element childElement : childElements) {
                if (childElement.attr("class").matches("center"))
                {
                    rates[i] = 0.0;
                    i++;
                } else 
                if (childElement.attr("class").equals("match"))
                {
                    text = childElement.text().trim();
                    System.out.print("\n" + text);
                    teams = childElement.text().split(" - ");
                    i = 0;
                } else 
                if (childElement.attr("class").matches("rate"))
                {
                    text = childElement.text().replace(",", ".");
                    System.out.print(" " + text);
                    rates[i] = Double.valueOf(text);
                    i++;
                }
            }
            
            
            if (i == 6) {
                if (teams.length == 2) {
                    insertRow(teams, rates);
                }
                i = 0;
                teams = null;
            }
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
}