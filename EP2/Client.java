import java.nio.channels.*;
import java.nio.file.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Bruno Ladeia <brunoladeia13@gmail.com>
 */
public class Client implements Runnable {
    private String search;                                                  // File to be researched in network.
    private int port;                                                       // The client's own address
    private static final int ADDRESSES[] = {9877, 9878, 9879, 9880, 9881,
                                            9882, 9883, 9884, 9885, 9886};  // Ports were used instead of Inet Address.
    private DatagramSocket ds;                                              // Socket que irá ficar ouvindo e esperando resposta de um peer
    
    // Default constructor
    public Client(){ }
    
    public Client(int port) throws SocketException {
        this.setPort(port);
        ds = new DatagramSocket(port);
    }

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DatagramSocket getDs() {
        return this.ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        try{
            this.receiveResponse();
        }
        catch(SocketTimeoutException e) {
            System.out.println("Tempo esgotado para a consulta por arquivo " + this.getSearch());
        }
        catch(SocketException e){
            System.err.println(e);
        }
        catch(UnknownHostException e){
            System.err.println(e);
        }
        catch(FileNotFoundException e){
            System.err.println(e);
        }
        catch(IOException e){
            System.err.println(e);
        }
        catch(ClassNotFoundException e){
            System.err.println(e);
        }
        /*catch(InterruptedException e) {
            System.err.println(e);
        }*/
    }

    /**
     * Realizar uma consulta - Primeiro método exigido para o cliente
     * */
    public void sendSearch() throws UnknownHostException, SocketException, IOException {
        // endereço IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        // // canal de comunicação não orientado à conexão
        // DatagramSocket clientSocket = new DatagramSocket();

        // Escolhendo peer aleatório da lista dos peers conhecidos
        Random r = new Random();
        int port = Client.ADDRESSES[r.nextInt(10)];

        // Passando o termo de busca para o buffer
        final byte[] sendData = this.getSearch().getBytes();

        // Criando pacote com base na porta selecionada aleatoriamente
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length,
                        IPAddress, port);

        this.getDs().send(sendPacket);
        System.out.println("Pesquisando por Arquivo " + this.getSearch() + " no peer " + port);
    }

    /** Procura pelo arquivo numa pasta de referência e, caso encontre, será baixado.
     *  <p> Método 3 solicitado para o cliente, 
     *  responsável por fazer download do arquivo.
     *  </p>
     *  @param file O endereco e nome do arquivo a ser baixado
     * */
    private void downloadFile(String file) throws MalformedURLException, IOException, FileNotFoundException {
        // Pega o endereco home da maquina
        String home = System.getProperty("user.home");

        // Pasta de referência definida no momento
        File video = new File(file);

        System.out.println("Baixando o arquivo " + this.getSearch() + " do Peer X");
        
        // Abre um novo canal de transmissao, com base no endereco home
        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(video.toURI().toString()).openStream());

        // Salva o arquivo na home do cliente
        FileOutputStream fileOutputStream = new FileOutputStream(home + "/Documents/UFABC/EP2/Client1/" + video.getName());
        FileChannel fileChannel = fileOutputStream.getChannel();

        // Realiza a transferência dos dados
        fileOutputStream.getChannel()
            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        System.out.println("Arquivo " + this.getSearch() + " baixado.");
    }

    
    protected void receiveResponse() throws SocketException, IOException, ClassNotFoundException {
        
        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        // Define um tempo limite de 10s
        this.getDs().setSoTimeout(10000);

        // Recebimento do datagrama do host remoto (método bloqueante)
        this.getDs().receive(data);

        // String com a resposta do peer (caso tenha recebido)
        String resp = new String(data.getData(), 0, data.getLength());
        
        // Faz o download do arquivo, com base no caminho de resposta
        this.downloadFile(resp);
    }

    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Digite 'bye' para sair...");
        System.out.print("Digite o nome do arquivo a ser pesquisado: ");
        String aux = scan.nextLine();

        try{
            // Iniciando o cliente na porta 10000
            Client c = new Client(10000);

            while(!aux.equals("bye")) {
                c.setSearch(aux);
            
                // Envia o termo de busca
                c.sendSearch();

                // Encerra de forma atencipada o programa para não travar
                if(aux.equals("bye")) break;

                // Inicia a thread e trava a UI
                (new Thread(c)).start();
                Thread.sleep(11000);

                System.out.println("Digite 'bye' para sair...");
                System.out.print("Digite o nome do arquivo a ser pesquisado: ");
                aux = scan.nextLine();
            }

            if(c.getDs().isConnected()) c.getDs().close();
        }
        catch(SocketTimeoutException e){
            System.out.println("Tempo esgotado para a consulta por arquivo " + aux);
        }
        catch(SocketException e){
            System.err.println(e);
        }
        /*catch(ClassNotFoundException e) {
            System.err.println(e);
        }*/
        catch(IOException e) {
            System.err.println(e);
        }
        catch(InterruptedException e) {
            System.err.println(e);
        }
    }
}