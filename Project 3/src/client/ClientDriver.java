package client;

import java.io.IOException;

import utils.PropertyHandler;

/**
 * Create multiple instances of Client to run in parallel, depending on client
 * properties.
 */
public class ClientDriver
{
    public static void main(String[] args)
    {
        try
        {
            PropertyHandler properties = new PropertyHandler("config/Client.properties");
            int numClients = Integer.parseInt(properties.getProperty("NUM_CLIENTS"));

            for (int i = 0; i < numClients; i++)
            {
                new Thread(new Client("config/Client.properties")).start();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
