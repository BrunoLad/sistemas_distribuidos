import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

/**
 *
 * @author bruno
 */
public class Peer implements Runnable {
    private String[] fileNames;                                                 // Nome dos arquivos que aquele peer possui e retira da pasta
    private int identifier;                                                     // Endereco do próprio peer
    private static final int ADDRESSES[] = {9878, 9879, 9880, 9881,
                                            9882, 9883, 9884, 9885, 9886};      // Portas foram utilizadas ao invés de Inet Address.
    private int client;                                                         // Endereco destino do cliente
    private final String home = System.getProperty("user.home") 
                                + "/Documents/UFABC/EP2/Peer1/Videos/";         // Repositorio Base Peer
    private int ttl;                                                            // Time to live atual do pacote
    private static final int TTLREFERENCE = 5;                                  // Time to live de referencia
    private ArrayList<String> searches; 
    private DatagramSocket serverSocket;                                        // Socket do servidor   

    public Peer() {
        searches = new ArrayList<String>();
        this.setTtl(Peer.TTLREFERENCE);
    }
    
    public Peer(int port) throws SocketException {
        searches = new ArrayList<String>();
        this.setTtl(Peer.TTLREFERENCE);
        this.setIdentifier(port);
        this.serverSocket = new DatagramSocket(port);
    }

    public String[] getFileNames() {
        return this.fileNames;
    }

    public void setFileNames(String[] files) {
        this.fileNames = files;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int port) {
        this.identifier = port;
    }

    public int getClient() {
        return this.client;
    }

    public void setClient(int destination) {
        this.client = destination;
    }

    public String getHome(){
        return this.home;
    }

    public int getTtl(){
        return this.ttl;
    }

    public void setTtl(int ttl){
        this.ttl = ttl;
    }

    public DatagramSocket getServerSocket() {
        return this.serverSocket;
    }

    public void setServerSocket(DatagramSocket ds) {
        this.serverSocket = ds;
    }

    @Override
    public void run() {
        try{
            // Espera por mensagens
            this.receiveStates();
        }
        catch(SocketException e) {
            System.err.println(e);
        }
        catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Obtém os metadados da pasta "home" do Peer
     */
    // Thread
    public void refreshMetaData() {
        File f = new File(this.getHome());

        this.setFileNames(f.list());
    }

    // Thread
    public void receiveStates() throws SocketException, IOException {
        // Socket que ira ficar ouvindo e esperando comunicacao do outro peer
        //DatagramSocket serverSocket = new DatagramSocket(this.getIdentifier());

        while (true) {

            byte[] recBuffer = new byte[1024];

            DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

            // Recebimento do datagrama do host remoto (metodo bloqueante)
            this.getServerSocket().receive(data);

            // Verifica se eh um endereco de um cliente
            boolean isPeer = checkAddress(data.getPort());

            String file;

            // Pega o endereco do cliente
            if(!isPeer){
                this.setClient(data.getPort());

                // Pega o nome do arquivo sendo buscado
                file = new String(data.getData(), 0, data.getLength());
            } else {
                
                // Extrai a mensagem completa para achar o indice do separador
                String msg = new String(data.getData(), 0, data.getLength());
                String[] msgaux = msg.split("#");

                // Pega o endereco do cliente
                this.setClient(Integer.parseInt(msgaux[1]));
                
                // Atualizo o ttl
                this.setTtl(Integer.parseInt(msgaux[2]) - 1);
                
                // Pega o nome do arquivo sendo buscado
                file = msgaux[0];
            }

            if(this.getTtl() != 0){
                if(this.getTtl() >= (Peer.TTLREFERENCE - 1) || !searches.contains(file)){

                    // Verifica a lista para ver se o elemento existe
                    boolean result = this.checkFileExists(file);

                    if(result){
                        System.out.println("Recebendo pesquisa " + file + ", tenho o arquivo " + file + " no meu estado.");
                        this.sendResponse(this.getClient(), file);
                    } else {
                        Random r = new Random();
                        int port = Peer.ADDRESSES[r.nextInt(9)];

                        // Caso ocorra de tirar o mesmo Peer de quem recebeu mensagem
                        // Escolhe outro destino
                        while(port == data.getPort()) {
                            port = Peer.ADDRESSES[r.nextInt(9)];
                        }
                        
                        // Adiciona que o arquivo já foi buscado anteriormente
                        this.searches.add(file);

                        System.out.println("Recebendo pesquisa " + file + ", NAO tenho o arquivo, encaminho para " + port);
                        
                        forwardResponse(port, file);
                    }
                } else {
                    System.out.println("Recebendo pesquisa " + file + ", MSG DUPLICADA NAO ENCAMINHO");
                    this.resetState();
                }
            } else {
                System.out.println("Recebendo pesquisa " + file + ", TTL=ZERO NAO ENCAMINHO");
                this.resetState();
            }
        }
    }

    public void sendResponse(int dest, String file) throws UnknownHostException, SocketException, IOException {
        // Endereco IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        // Canal de comunicação não orientado à conexão
        //DatagramSocket serverSocket = new DatagramSocket();

        // Passa o caminho do arquivo
        final byte[] sendData = (this.home + file).getBytes();

        // Criando pacote com base no endereco do cliente
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length,
                        IPAddress, dest);

        this.getServerSocket().send(sendPacket);
    }

    public void forwardResponse(int dest, String file) throws UnknownHostException, SocketException, IOException {

        // Endereco IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        // Canal de comunicação não orientado à conexão
        //DatagramSocket serverSocket = new DatagramSocket();

        // Montando a mensagem a ser enviada
        String msg = file + "#" + this.getClient() + "#" + this.getTtl();

        // Preenche o buffer de envio
        final byte[] sendData = msg.getBytes();

        // Montando o pacote com o offset do tamanho do nome do arquivo.
        DatagramPacket sendPacket 
                = new DatagramPacket(sendData, sendData.length,
                        IPAddress, dest);

        this.getServerSocket().send(sendPacket);

        // Reseta o ttl para o Peer em questao
        //this.setTtl(Peer.TTLREFERENCE);
    }

    private boolean checkFileExists(String file){

        for(int i = 0; i < this.fileNames.length; i++){
            if(this.fileNames[i].equals(file)) return true;
        }

        return false;
    }

    private boolean checkAddress(int add) {
        
        for(int i = 0; i < Peer.ADDRESSES.length; i++){
            if(Peer.ADDRESSES[i] == add) return true;
        }

        return false;
    }

    private void resetState() {
        this.setTtl(Peer.TTLREFERENCE);
        this.searches.clear();
    }

    public static void main(String[] args) {
        
        try{
            Peer p = new Peer(9877);

            (new Thread(p)).start();

            while(true) {

                System.out.println("Obtendo informacoes da pasta");
                // Pega os arquivos na pasta a cada 5s
                p.refreshMetaData();
                Thread.sleep(5000);
            }
        }
        catch(InterruptedException e) {
            System.err.println(e);
        }
        catch(SocketException e) {
            System.err.println(e);
        }
    }
}