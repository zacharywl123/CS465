package appserver.client;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import appserver.job.Job;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;

/**
 * Class [FibonacciClient] client uses the Fibonacci tool
 * 
 * @author Zachary
 */
public class FibonacciClient extends Thread{
    
    /**
     * Input parameter to get fib result from server
     */
    private int parameter;
    
    /**
     * IP and port to connect to server
     */
    String host = null;
    int port;

    /**
     * Used to load in properties file
     */
    Properties properties;
    
    public FibonacciClient(String config, int index)
    {
        // read config file
                try {
            properties = new PropertyHandler(config);
            host = properties.getProperty("HOST");
            System.out.println("[FibonacciClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[FibonacciClient] Port: " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // set our parameter to give to Fib as the index from which the client was created
        parameter = index;
    }
    
    @Override
    public void run()
    {
        try { 
            // connect to application server
            Socket server = new Socket(host, port);
            
            // hard-coded string of class, aka tool name ... plus one argument
            String classString = "appserver.job.impl.Fibonacci";
            
            // create job and job request message
            Job job = new Job(classString, parameter);
            Message message = new Message(JOB_REQUEST, job);
            
            // sending job out to the application server in a message
            ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
            writeToNet.writeObject(message);
            
            // reading result back in from application server
            // for simplicity, the result is not encapsulated in a message
            ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
            Integer result = (Integer) readFromNet.readObject();
            
            System.out.println("RESULT: " + result);
            System.out.printf("Fibonacci of %d: %d\n", parameter, result);
        } catch (Exception ex) {
            System.err.println("[FibonacciClient.run] Error occurred");
            ex.printStackTrace();
        }
    }
    
    // Create fib clients to interact with the server
    public static void main(String[] args)
    {
        int index;
        for (index=46; index>0; index--)
        {
            new Thread(new FibonacciClient("../../config/Server.properties", index)).start();
        }
    }
}
