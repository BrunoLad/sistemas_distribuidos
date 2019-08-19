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

    public Reducer() throws SocketException {
        this. ds = new DatagramSocket(10000);
    }

    public DatagramSocket getDs() {
        return this.ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public void Receive() {
        
    }
}
