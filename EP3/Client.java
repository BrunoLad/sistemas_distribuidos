
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
public class Client implements Runnable {

    //Lembrar de pegar endereço + porta do coordinator
    private ArrayList<String> links;                                // Estrutura que contem os links
    private static final String HOME = System.getProperty("user.home")
            + "/Documents/UFABC/links/";                            // Repositorio base do cliente, para obter links
    private DatagramSocket ds;
    private InetAddress address;

    Client() throws SocketException {
        this.ds = new DatagramSocket(10000);
        this.links = new ArrayList<String>();
    }

    Client(int port) throws SocketException {
        this.ds = new DatagramSocket(port);
        this.links = new ArrayList<String>();
    }
    
    // Getter e Setters
    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    /**
     * Método para obter os links de um arquivo. Os armazena num arraylist.
     */
    public void getFiles() throws IOException {
        File f = new File(Client.HOME + "links.txt");

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            links.add(line);
        }
    }

    public void sendLinks() throws UnknownHostException, IOException {
        // endereço IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("192.168.15.13");

        String msg = this.createMessage();

        final byte[] sendData = msg.getBytes();

        // Criando pacote com base na porta selecionada aleatoriamente
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length,
                        IPAddress, 10000);

        this.getDs().send(sendPacket);

        System.out.println("Enviando lista L com " + this.getLinks().size());
    }

    public void receiveResponse() throws SocketException, IOException, ClassNotFoundException {

        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do host remoto (método bloqueante)
        this.getDs().receive(data);

        // String com a resposta do peer (caso tenha recebido)
        String resp = new String(data.getData(), 0, data.getLength());

        // Sera preciso quebrar primeiro por estrutura macro
        // Depois quebrar por elementos dentro daquelas estruturas
        String[] respAux = resp.split("#");

        this.saveMessage(respAux);

        System.out.println("Indice invertido recebido, armazenado na pasta " + Client.HOME);
    }

    private String createMessage() {
        String msg = "";

        for (String link : this.links) {
            msg = msg + link + "#";
        }

        // Retirando o ultimo #
        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

    /**
     * Apos receber a resposta, salva a informacao num novo arquivo
     *
     * @param msgs
     * @throws IOException
     */
    private void saveMessage(String[] msgs) throws IOException {
        this.links.clear();

        // this.links.addAll(Arrays.asList(msgs));
        File f = new File(Client.HOME + "/respostaLinks.txt");

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        for (String s : msgs) {
            bw.write(s);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.receiveResponse();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            Client c = new Client();

            (new Thread(c)).start();

            while (true) {
                c.getFiles();
                c.sendLinks();

                // Trava a UI e a cada 10s le do arquivo e envia as infos
                Thread.sleep(10000);
            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (InterruptedException e) {
            System.err.println(e);
        }

    }
}
