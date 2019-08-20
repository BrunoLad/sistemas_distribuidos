import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

/**
 *
 * @author Bruno Ladeia <brunoladeia13@gmail.com>
 */
public class Reducer {
    private DatagramSocket ds;
    private InetAddress address;

    public Reducer() throws SocketException {
        this. ds = new DatagramSocket(10000);
    }
    
    public Reducer(int port) throws SocketException {
        this.ds = new DatagramSocket(port);
    }
    
    public Reducer(String address) throws SocketException, UnknownHostException {
        this.ds = new DatagramSocket(10000);
        this.address = InetAddress.getByName(address);
    }
    
    public Reducer(int port, String address) throws SocketException, UnknownHostException {
        this.ds = new DatagramSocket(port);
        this.address = InetAddress.getByName(address);
    }

    public DatagramSocket getDs() {
        return this.ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    public void setAddress(InetAddress add) {
        this.address = add;
    }

    public void receive() {
        
    }
    
    public void replyClient() {
        
    }
}