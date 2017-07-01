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

/**
 *
 * @author adam
 */
public class Stavka {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        URL website = new URL("https://www.ifortuna.sk/sk/stavkovanie/futbal?nolimit");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("fortuna.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        Fortuna fortuna = new Fortuna();
    }
    
}
