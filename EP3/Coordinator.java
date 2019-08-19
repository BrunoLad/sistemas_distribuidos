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
public class Coordinator implements Runnable {
    private Mapper[] m;                         //Endereço e informacoes dos mappers
    private DatagramSocket ds;
    
    /**
     * Construtor por padrão iniciando o canal de comunicação na porta 10000
     */
    public Coordinator() throws SocketException {
        this.m = new Mapper[3];
        this.ds = new DatagramSocket(10000);
    }

    public Coordinator(int numMaps) throws SocketException {
        this.m = new Mapper[numMaps];
        this.ds = new DatagramSocket(10000);
    }

    public Coordinator(int port) throws SocketException {
        this.m = new Mapper[3];
        this.ds = new DatagramSocket(port);
    }

    public Coordinator(int numMaps, int port) throws SocketException {
        this.m = new Mapper[numMaps];
        this.ds = new DatagramSocket(port);
    }

    public Mapper[] getM() {
        return m;
    }

    public void setM(Mapper[] m) {
        this.m = m;
    }

    public DatagramSocket getDs(){
        return this.ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }
    
    @Override
    public void run() {

    }

    public void receive() {
        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do host remoto (método bloqueante)
        this.getDs().receive(data);

        int clientPort = data.getPort();

        // String com a resposta do peer (caso tenha recebido)
        String resp = new String(data.getData(), 0, data.getLength());
        
        // Pega os links recebidos pelo Cliente e quebra
        String[] respAux = resp.split("#");
        
        this.saveMessage(respAux);
    }

    public void forwardToMappers() {

    }
}
