import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Bruno Ladeia <brunoladeia13@gmail.com>
 */
public class Mapper implements Runnable {
    private Reducer r[];                        // Informacao sobre o endereco do Reducer
    private DatagramSocket ds;                  // Socket de conexao do Mapper
    private InetAddress address;                // Endereco do proprio Mapper
    private ArrayList<Link> links;              // Estrutura que armazena os enderecos baixados da urls remotas
    private int port;                           // Porta de acesso para o Mapper

    public Mapper() throws SocketException { 
        this.links = new ArrayList<>();
        this.r = new Reducer[1];
        this.ds = new DatagramSocket(10000);
    }

    public Mapper(int numReds) throws SocketException {
        this.links = new ArrayList<>();
        this.r = new Reducer[numReds];
        this.ds = new DatagramSocket(10000);
    }
    
    public Mapper(String address) throws SocketException, UnknownHostException {
        this.links = new ArrayList<>();
        this.r = new Reducer[1];
        this.ds = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public Mapper(int numReds, int port) throws SocketException {
        this.links = new ArrayList<>();
        this. r = new Reducer[numReds];
        this.ds = new DatagramSocket(port);
    }
    
    public Mapper(int numReds, int port, String address) throws SocketException, UnknownHostException {
        this.links = new ArrayList<>();
        this.r = new Reducer[numReds];
        this.ds = new DatagramSocket(port);
        this.address = InetAddress.getByName(address);
    }

    public Reducer[] getR() {
        return this.r;
    }

    public void setR(Reducer[] r) {
        this.r = r;
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
    
    public ArrayList<String> getLinks() {
        return this.links;
    }
    
    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void receive() throws IOException {
        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do coordenador remoto (método bloqueante)
        this.getDs().receive(data);

        // String com a resposta do peer (caso tenha recebido)
        String resp = new String(data.getData(), 0, data.getLength());
        
        // Pega os links recebidos pelo Coordenador e os separa
        // Na ultima posicao havera o endereco do cliente
        String[] respAux = resp.split("#");

        String[] dadosCliente = respAux[respAux.length - 1].split(":");

        // Pega o endereço e porta do cliente que está na última posição
        InetAddress clientAddress = InetAddress.getByName(dadosCliente[0])
        int clientPort = Integer.parseInt(dadosCliente[1]);
        
        // Pegando todas as urls, menos a ultima que contem o endereço do cliente
        this.readFromURL(Arrays.copyOf(respAux, respAux.length-1));

        // Imprime mensagem no console do coordenador
        System.out.println("Recebendo lista L com " + (respAux.length - 1) + " URLs ... processando");
    }

    public void forwardToReducers() {

    }
    
    private void readFromURL(String[] linksRef) throws MalformedURLException, IOException {
        
        for(String link : linksRef) {
            // Inicializa a estrutura com cada endereço recebido
            Link l = new Link(link);

            URL siteRef = new URL(link);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(siteRef.openStream()));
            
            String inputLine;
            
            while((inputLine = in.readLine()) != null){
                
            }
        }
    }
    
    @Override
    public void run() {
        
    }
    
    public static void main(String[] args) {
        
    }
}