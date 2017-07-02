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
public class Fortuna {
    
    private static final String tableName = "FORTUNA";
    private static int counter = 1;
    
    public Fortuna () {
        
        String[] teams = null;
        double[] rates = new double[6];
        String text;
        int i = 0;


        File input = new File("fortuna.txt");
        Document doc = null;
        
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Elements links = doc.select("td[class]");
        Elements childElement = null;

        System.out.print("Links: " + links.size());
        for (Element link : links) {
            if (link.attr("class").equals("col_bet  col_bet_empty"))
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
                    teams = childElement.text().split(" - ");
                    i = 0;
                }

                if (childElement.attr("class").matches("add_bet_link betlink-(.*)"))
                {
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