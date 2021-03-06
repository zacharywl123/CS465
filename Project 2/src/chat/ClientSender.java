package chat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import message.Message;

/**
 * Handles the sending of messages by the Client to the Server
 *
 * @author Zachary Wilson-Long
 */
public class ClientSender extends Thread
{
    /**
     * Objects used to handle server connectivity
     */
    Socket chatConnection = null;
    ObjectOutputStream toChat = null;

    /**
     * Server info
     */
    String receiverIP;
    int receiverPort;
    String logicalName;
    Message message;

    /**
     * Object used for reading input
     */
    Scanner userInput;

    /**
     * Constructor that sets up sender thread
     *
     * @param receiverIP    ip of message receiver
     * @param receiverPort  port of message receiver
     * @param logicalName   logical name of client/user
     * @param message       object containing message to send
     */
    public ClientSender(String receiverIP, int receiverPort, String logicalName, Message message)
    {
        this.receiverIP = receiverIP;
        this.receiverPort = receiverPort;
        this.logicalName = logicalName;
        this.message = message;

        // open up stdin
        userInput = new Scanner(System.in);
    }

    /**
     * closes connection with receiver
     */
    public void closeConnection()
    {
        try
        {
            chatConnection.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Cannot close connection", ex);
            System.exit(1);
        }
    }

    /**
     * Handles server connection for the client
     * 
     * @throws IOException
     */
    public void connectToReceiver() throws IOException
    {
        chatConnection = new Socket(receiverIP, receiverPort);
        toChat = new ObjectOutputStream(chatConnection.getOutputStream());
    }

    /**
     * Processes user input and sends to the server.
     */
    @Override
    public void run()
    {
        // Send message to user
        try
        {
            sendMessageToUser();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Method from online which gets the system's IP address
     *
     * @return IP address, or null if an error occurred
     */
    public String getMyIP()
    {
        try
        {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()
                    || networkInterface.isVirtual() || networkInterface.isPointToPoint())
                {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    InetAddress address = addresses.nextElement();

                    final String myIP = address.getHostAddress();
                    if (Inet4Address.class == address.getClass())
                    {
                        return myIP;
                    }
                }
            }
        }
        catch (SocketException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Sends message to connected chat user.
     */
    public void sendMessageToUser() throws IOException
    {
        // Connect to other client
        connectToReceiver();

        // Write object to other client
        toChat.writeObject(message);

        // close connection to server after sending message
        closeConnection();
    }
}

