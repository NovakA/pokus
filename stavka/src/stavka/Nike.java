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
public class Nike {
    
    private static final String tableName = "NIKE";
    private static int counter = 1;
    
    public Nike () {
        
        String[] teams = new String[2];
        double[] rates = new double[6];
        String text;
        int i = 0;


        File input = new File("nike.txt");
        Document doc = null;
        
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Fortuna.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Elements links = doc.select("td[class]");
        Elements childElements = null;

        System.out.print("Links: " + links.size());
        for (Element link : links) {
            if (link.attr("class").equals("super"))
            {
                childElements = link.select("span");
                for (Element childElement : childElements) {
                    if (childElement.attr("class").equals("super-s1 home "))
                        teams[0] = childElement.text();
                    
                    if (childElement.attr("class").equals("super-s1 away"))
                        teams[1] = childElement.text();
                }
                i = 0;
            }
            
            if (link.attr("class").matches("type (.*)") && teams[1] != null)
            {
                childElements = link.select("a");
                for (Element childElement : childElements) {
                    if (childElement.attr("tipId").matches("TIP_."))
                    {
                        rates[i] = Double.valueOf(childElement.text());
                        i++;
                    }
                }
            }
            
            
            if (i == 6) {
                if (teams.length == 2) {
                    System.out.print("\n" + teams[0] + " - ");
                    System.out.print(teams[1]);
                    for (int j = 0; j < 6; j++) System.out.print( " " + rates[j]);
                    
                    insertRow(teams, rates);
                }
                i = 0;
                teams[0] = null;
                teams[1] = null;
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