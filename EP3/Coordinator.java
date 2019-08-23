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
    private final InetAddress address = "";
    
    /**
     * Construtor por padrão iniciando o canal de comunicação na porta 10000
     */
    public Coordinator() throws SocketException {
        this.m = new Mapper[3];
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

    public InetAddress getAddress() {
        return this.address;
    }
    
    @Override
    public void run() {
        while(true){
            try {
                this.receive();
            }
            catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
            }
        }
    }

    public void receive() throws IOException {
        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do host remoto (método bloqueante)
        this.getDs().receive(data);

        int clientPort = data.getPort();
        InetAddress address= data.getAddress();

        // String com a resposta do peer (caso tenha recebido)
        String resp = new String(data.getData(), 0, data.getLength());
        
        // Pega os links recebidos pelo Cliente e quebra
        String[] respAux = resp.split("#");
        
        // Imprime mensagem no console do coordenador
        System.out.println("Recebendo lista L com " + respAux.length + " URLs");
        
        String[] finalMsg = this.divideList(respAux, address, clientPort);
        
        this.forwardToMappers(finalMsg);
    }

    private void forwardToMappers(String[] msg) throws IOException {
        // Envia mensagem para cada Mapper que o Coordenador conheça
        for(int i = 0; i < m.length; i++) {
            final byte[] sendData = msg[i].getBytes();
            
            // Criando pacote com base na porta selecionada aleatoriamente
            DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length,
                        m[i].getAddress(), 10000);
            
            this.getDs().send(sendPacket);
            
            System.out.println("Enviando " + (msg[i].split("#").length - 1) +" para o mapper" + i);
        }
    }
    
    private String[] divideList(String[] msg, InetAddress address, int port) {
        String[] aux = new String[this.m.length];
        
        // Alterando o valor null para padrao da String
        for(String s : aux) {
            s = "";
        }
        
        int j = 0;
        
        // Divide de forma "igual" os enderecos
        // E adiciona o endereco do cliente ao final de cada expressao
        for (int i = 0; i < msg.length; i++) {
            
            if((msg.length - i) == m.length){
                aux[j] += address.getHostAddress() + ":" + port;
            } 
            else {
            
                aux[j] += msg[i] + "#";
                j++;
            
                if(j == m.length) j = 0;
            }
        }
        
        return aux;
    }
    
    public static void main(String[] args) {
        try {
            Coordinator c = new Coordinator();
            
            (new Thread(c)).start();
        }
        catch(SocketException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}