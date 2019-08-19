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
public class Mapper {
    private Reducer r[];
    private DatagramSocket ds;

    public Mapper() throws SocketException { 
        this.r = new Reducer[1];
        this.ds = new DatagramSocket(10000);
    }

    public Mapper(int numReds) throws SocketException {
        this.r = new Reducer[numReds];
        this.ds = new DatagramSocket(10000);
    }

    public Mapper(int port) throws SocketException {
        this.r = new Reducer[1];
        this.ds = new DatagramSocket(port);
    }

    public Mapper(int numReds, int port) throws SocketException {
        this. r = new Reducer[numReds];
        this.ds = new DatagramSocket(port);
    }

    public Reducer[] getR() {
        return this.r;
    }

    public void setR(Reducer r) {
        this.r = r;
    }

    public DatagramSocket getDs() {
        return this.ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public void receive() {

    }

    public void forwardToReducers() {

    }
}
