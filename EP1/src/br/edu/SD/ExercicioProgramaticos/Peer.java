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

    public Peer() {
        this.version = 0;
        this.counter = 0;
        this.z = new Peer[4];
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
    private void readFromFile() throws IOException {
        // Local onde o arquivo eh extraido
        Path origin = Paths.get("/home/bruno/NetBeansProjects/EP1"
                + "/src/br/edu/SD/ExercicioProgramaticos/teste/teste.txt");

        // Atualiza as informações do objeto de acordo com a leitura.
        this.setFile(origin.getFileName().toString());
        this.setVersion(this.getVersion() + 1);

        // Imprime a mensagem, mostrando que a thread foi executada
        System.out.println("Arquivo " + this.getFile() + " obtido da pasta."
                + " Estado atual de X " + this.getVersion());
    }

    // Envio do T2
    private void sendOwnState() throws UnknownHostException, SocketException, IOException, CloneNotSupportedException {
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

        // Criação do Datagrama na porta 9876
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Envia pacote e imprima mensagem
        System.out.println("Envio " + p.getVersion() + " por gossip ao peer W.");
        clientSocket.send(sendPacket);
    }

    // Recebimento do T2 e, por enquanto, T3
    private void receiveStates() throws SocketException, IOException, ClassNotFoundException {
        //Socket que ira ficar ouvindo e esperando comunicacao de outro peer
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] recBuffer = new byte[1024];

        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);

        //recebimento do datagrama do host remoto (método bloqueante)
        serverSocket.receive(data);

        // Leitor para extrair as informações do objeto
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getData());
        final ObjectInputStream ois = new ObjectInputStream(bais);

        Factory fa = (Factory) ois.readObject();
        Peer recebimento = fa.getPeer();
        recebimento.setReceive(new Date());
        System.out.println("Arquivo: " + recebimento.getFile());
        System.out.println("Versão: " + recebimento.getVersion());
        System.out.println("Data de chegada: " + recebimento.getReceive().toString());
        this.z[this.counter] = recebimento;
        counter++;

        // fechamento da conexão
        serverSocket.close();
    }

    // Envio do T3
    private void sendOtherState() throws SocketException, IOException, ClassNotFoundException {
        if (this.getCounter() != 0) {
            Random rand = new Random();

            // Endereco IP do host remoto (server)        
            InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

            // Canal de comunicação não orientado à conexão
            DatagramSocket clientSocket = new DatagramSocket();

            //Criando clone da istância para que possa ser enviado via rede
            Peer p = this.z[rand.nextInt(this.getCounter())];

            // Inicializando classe wrapper com Peer a ser enviado
            Factory fa = new Factory(p);

            // Leitor para armazenar as informações do objeto
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fa);

            // Declaração e preenchimento do buffer de envio
            final byte[] sendData = baos.toByteArray();

            // Need to pick a random port from a selection of ports
            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

            //Envia o pacote
            System.out.println("Envio " + p.getVersion() + " por gossip ao peer Z.");
            clientSocket.send(sendPacket);
        }
    }
    
    // T4 - Apagará os estados que são muito antigos
    private void deleteStates() {
        
    }

    public static void main(String[] args) {
        Peer p1 = new Peer();

        try {
            p1.readFromFile();
            p1.sendOwnState();
            p1.receiveStates();
        } catch (SocketException e) {
            System.err.println(e);
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException | ClassNotFoundException | CloneNotSupportedException e) {
            System.err.println(e);
        }
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

// Classe para encapsular o objeto de Peer e enviar pela rede
class Factory implements Serializable {

    private Peer peer;

    public Factory() {
    }

    public Factory(Peer p) {
        this.setPeer(p);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer p) {
        this.peer = p;
    }
}
