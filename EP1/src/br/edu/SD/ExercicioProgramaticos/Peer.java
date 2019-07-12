/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.SD.ExercicioProgramaticos;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author bruno
 */
public class Peer implements Serializable, Cloneable {

    private Peer z[];       // Infos outros peers
    private String file;    // Metadado do arquivo
    private int version;    // Versao de leitura e da mensagem
    private Date receive;   // Apenas utilizado pelo peers em Z (T4)
    private int counter;    // Quantidade de outros peers armazenados
    private int identifier; // Will store portNumber as unique identifier
    private static final int PORTS[] = {9877, 9878, 9879, 9880}; // Endereços dos demais peers

    public Peer() {
        this.version = 0;
        this.counter = 0;
        this.z = new Peer[4];
        //this.identifier = 9876;
    }

    public Peer(int port) {
        this.version = 0;
        this.counter = 0;
        this.z = new Peer[4];
        this.identifier = port; //Iniciando este peer na porta passada
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Atribuindo a copia rasa para a variavel de ref. p
        Peer p = (Peer) super.clone();

        p.z = new Peer[4];

        // Criar novo objeto para o campo z
        // e atribui a copia rasa obtida,
        // para torná-la uma copia profunda
        return p;
    }

    // T1
    protected String readFromFile() throws IOException {
        // Local onde o arquivo eh extraido
        Path origin = Paths.get("/home/bruno/NetBeansProjects/EP1"
                + "/src/br/edu/SD/ExercicioProgramaticos/teste/teste.txt");

        // Atualiza as informações do objeto de acordo com a leitura.
        this.setFile(origin.getFileName().toString());
        this.setVersion(this.getVersion() + 1);

        // Retorna a mensagem, mostrando que a thread foi executada
        return "Arquivo " + this.getFile() + " obtido da pasta."
                + " Estado atual de X " + this.getVersion();
    }

    // Envio do T2
    protected String sendOwnState() throws UnknownHostException, SocketException, IOException, CloneNotSupportedException {
        // endereço IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        // canal de comunicação não orientado à conexão
        DatagramSocket clientSocket = new DatagramSocket();

        //Criando clone do istância inicial para que possa ser enviado via rede
        Peer p = (Peer) this.clone();

        // Inicializando classe wrapper e passando o objeto nela
        Factory fa = new Factory(p);

        // Leitor para armazenar as informações do objeto
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fa);

        // Declaração e preenchimento do buffer de envio
        final byte[] sendData = baos.toByteArray();

        Random rand = new Random();
        int port = Peer.PORTS[rand.nextInt(4)];

        // Criação do Datagrama numa porta aleatoriamente escolhida
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length,
                        IPAddress, port);

        // Envia pacote e retorna mensagem
        clientSocket.send(sendPacket);
        return "Envio estado " + p.getFile() + " versao " + p.getVersion()
                + " do endereco " + this.getIdentifier()
                + " por gossip ao peer W. (" + port + ")";
    }

    // Recebimento do T2 e T3, thread separada para ficar escutando as mensagens
    protected void receiveStates() throws SocketException, IOException, ClassNotFoundException {
        // Socket que ira ficar ouvindo e esperando comunicacao de outro peer
        DatagramSocket serverSocket = new DatagramSocket(this.getIdentifier());

        while (true) {

            byte[] recBuffer = new byte[1024];

            DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

            // Recebimento do datagrama do host remoto (método bloqueante)
            serverSocket.receive(data);

            // Leitor para extrair as informações do objeto
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getData());
            final ObjectInputStream ois = new ObjectInputStream(bais);

            // Fazendo unwrap do Peer recebido e atualizando seu horario de recebimento
            // com base no horário do Peer destino
            Factory fa = (Factory) ois.readObject();
            Peer recebimento = fa.getPeer();
            recebimento.setReceive(new Date());

//        System.out.println("Arquivo: " + recebimento.getFile());
//        System.out.println("Versão: " + recebimento.getVersion());
//        System.out.println("Data de chegada: " + recebimento.getReceive().toString());
            if (recebimento.getIdentifier() == this.getIdentifier()) {
                String msg = recebimento.getVersion() == this.getVersion()
                        ? "Recebimento DUPLICADO " + recebimento.getVersion() + " do estado " + recebimento.getFile()
                        + " vindo do peer X. (" + data.getPort() + ")"
                        : "Recebimento ANTIGO " + recebimento.getVersion() + " do estado " + recebimento.getFile()
                        + " vindo do peer X. (" + data.getPort() + ")";

                System.out.println(msg);
            } else {
                for (int i = 0; i < this.getCounter(); i++) {
                    Peer ref = this.z[i];

                    //Verifica se o peer ja foi armazenado antes
                    if (recebimento.getIdentifier() == ref.getIdentifier()) {
                        if (recebimento.getVersion() > ref.getVersion()) {
                            this.z[i] = recebimento;
                        } else {
                            String msg = recebimento.getVersion() == ref.getVersion()
                                    ? "Recebimento DUPLICADO " + recebimento.getVersion() + " do estado " + recebimento.getFile()
                                    + " vindo do peer X. (" + data.getPort() + ")"
                                    : "Recebimento ANTIGO " + recebimento.getVersion() + " do estado " + recebimento.getFile()
                                    + " vindo do peer X. (" + data.getPort() + ")";

                            System.out.println(msg);
                        }
                    }
                }
                if (this.getCounter() != (this.z.length - 1)) {
                    // Adiciona o peer na ultima posicao
                    this.z[this.getCounter()] = recebimento;
                    this.counter++;
                }
            }

            System.out.println("Recebimento estado "
                    + recebimento.getFile() + " versao "
                    + recebimento.getVersion()
                    + " por gossip vindo do peer X. (" + recebimento.getIdentifier() + ")");
        }
        //Fechamento da conexao
        //serverSocket.close();
        //return "Encerrando...";
    }

    // Envio do T3
    protected String sendOtherState() throws SocketException, IOException, ClassNotFoundException {
        if (this.getCounter() != 0) {
            Random rand = new Random();

            // Endereco IP do host remoto (server)
            InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

            // Canal de comunicacao nao orientado a conexao
            DatagramSocket clientSocket = new DatagramSocket();

            //Criando clone da istancia para que possa ser enviado via rede
            Peer p = this.z[rand.nextInt(this.getCounter())];

            // Inicializando classe wrapper com Peer a ser enviado
            Factory fa = new Factory(p);

            // Leitor para armazenar as informacoes do objeto
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fa);

            // Declaracao e preenchimento do buffer de envio
            final byte[] sendData = baos.toByteArray();

            int port = Peer.PORTS[rand.nextInt(4)];

            // Need to pick a random port from a selection of ports
            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length,
                            IPAddress, port);

            //Envia o pacote
            clientSocket.send(sendPacket);
            return "Envio estado " + p.getFile() + " versao " + p.getVersion()
                    + " do endereco " + this.getIdentifier()
                    + " por gossip ao peer Z. (" + port + ")";
        }
        return "Pacote nao enviado, o Peer nao possui "
                + "informacao de nenhum outro peer";
    }

    // T4 - Apagará os estados que são muito antigos
    protected void deleteStates() {
        int eliminados = 0;

        for (int i = 0; i < this.counter; i++) {
            if (this.z[i] != null) {
                // Calcula a diferenca entre tempo de quando a ultima msg foi recebida e agora
                long deltaTime = (new Date()).getTime() - this.z[i].getReceive().getTime();

                //getTime retorna millisegundos, assume-se 10 segundos a baixo
                if (deltaTime >= 10000) {
                    // Informa que os estados de alguns peers estão sendo eliminados
                    System.out.println("Eliminando estados do peer W, Z");
                    System.out.println("Estado do peer " + this.z[i].getIdentifier() + " eliminado.");

                    eliminados++;

                    // Remove a referencia para a mensagem e atualiza o contador do peer
                    this.z[i] = null;
                    this.setCounter(this.getCounter() - 1);

                    this.arrangePeers(i);
                }
            }
        }
        System.out.println(eliminados + " peers foram eliminados");
    }

    private void arrangePeers(int pos) {
        // Verifica se nao eh a ultima posicao
        if (pos != (this.z.length - 1)) {
            // Atrasa o vetor para o posicionamento correto
            while (this.z[pos + 1] != null) {
                // Recuo uma posicao
                this.z[pos] = this.z[pos + 1];
                pos++;
            }
        }
    }

    public static void main(String[] args) {
        // Criando o peer de referencia na porta especificada
        Peer p1 = new Peer(9876);

        // Criando threads para cada um dos metodos
        PeerThread t0 = new PeerThread("T0", p1);
        PeerThread t1 = new PeerThread("T1", p1);
        PeerThread t2 = new PeerThread("T2", p1);
        PeerThread t3 = new PeerThread("T3", p1);
        PeerThread t4 = new PeerThread("T4", p1);

        /*try {
        
        }
        catch (SocketException e) {
            System.err.println(e);
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException | ClassNotFoundException | CloneNotSupportedException e) {
            System.err.println(e);
        }*/
    }

    public Peer[] getZ() {
        return z;
    }

    public void setZ(Peer[] z) {
        this.z = z;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int send) {
        this.version = send;
    }

    public Date getReceive() {
        return receive;
    }

    public void setReceive(Date receive) {
        this.receive = receive;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
