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
public class Peer extends Thread implements Serializable, Cloneable{
    private Peer z[]; // infos outros peers
    private String file;
    private int send;
    private Date receive; // Apenas utilizado para os outros peers (T4)
    private int counter; // quantidade de outros peers armazenados
    
    public Peer () {
        this.send = 0;
        this.counter = 0;
        this.z = new Peer[4];
    }
    
    @Override
    public void run() {
        
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        // Assigning shallow copy to new reference variable p
        Peer p = (Peer)super.clone();
        
        p.z = new Peer[4];
        
        // Create a new object for the field c
        // and assign it to shallow copy obtained,
        // to make it a deep copy
        return p;
    }
    
    //T1
    private void readFromFile() throws IOException{
        Path origin = Paths.get("/home/bruno/NetBeansProjects/EP1"
                + "/src/br/edu/SD/ExercicioProgramaticos/teste/teste.txt");
        this.file = origin.getFileName().toString();
    }
    
    // Envio do T2
    private void sendOwnState() throws UnknownHostException, SocketException, IOException{
        // endereço IP do host remoto (server)        
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
        // canal de comunicação não orientado à conexão
        DatagramSocket clientSocket = new DatagramSocket();
        
        // Inicializando classe wrapper
        Factory fa = new Factory(this);
        
        // Leitor para armazenar as informações do objeto
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fa);
        
        // Declaração e preenchimento do buffer de envio
        final byte[] sendData = baos.toByteArray();
        
        // Criação do Datagrama na porta 9876
        DatagramPacket sendPacket = 
                new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        
        // Atualização do contador de versões
        System.out.println("Enviando informacao para servidor");
        clientSocket.send(sendPacket);
        this.setSend(this.getSend() + 1);
    }
    
    // Recebimento do T2
    private void receiveStates() throws SocketException, IOException, ClassNotFoundException{
        //Socket que ira ficar ouvindo e esperando comunicacao de outro peer
        DatagramSocket serverSocket = new DatagramSocket(9876);
        
        byte[] recBuffer = new byte[1024];
        
        DatagramPacket data = new DatagramPacket(recBuffer, recBuffer.length);
        
        //recebimento do datagrama do host remoto (método bloqueante)
        serverSocket.receive(data);
        
        // Leitor para extrair as informações do objeto
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getData());
        final ObjectInputStream ois = new ObjectInputStream(bais);
        
        Factory fa = (Factory)ois.readObject();
        Peer recebimento = fa.getPeer();
        recebimento.setReceive (new Date());
        System.out.println("Arquivo: " + recebimento.getFile());
        System.out.println("Versão: " + recebimento.getSend());
        this.z[this.counter] = recebimento;
        counter++;
        
        // fechamento da conexão
        serverSocket.close();
    }
    
    // Envio do T3
    private void sendOtherState() throws SocketException, IOException, ClassNotFoundException{
        // endereço IP do host remoto (server)        
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
        // canal de comunicação não orientado à conexão
        DatagramSocket clientSocket = new DatagramSocket();
        
        // Inicializando classe wrapper
        Factory fa = new Factory(this);
        
        // Leitor para armazenar as informações do objeto
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fa);
        
        // Declaração e preenchimento do buffer de envio
        final byte[] sendData = baos.toByteArray();
        
        // Criação do Datagrama na porta 9876
        DatagramPacket sendPacket = 
                new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        
        // Atualização do contador de versões
        System.out.println("Enviando informacao para servidor");
        clientSocket.send(sendPacket);
        this.setSend(this.getSend() + 1);
    }
    
    
    public static void main(String[] args) {
        Peer p1 = new Peer();
        
        try {
            p1.readFromFile();
            p1.sendOwnState();
            p1.receiveStates();
        } catch(SocketException e){
            System.err.println(e);
        } catch(UnknownHostException e){
            System.err.println(e);
        } catch(IOException | ClassNotFoundException e){
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

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
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
}

// Classe para encapsular o objeto de Peer e enviar pela rede
class Factory implements Serializable {
    private Peer peer;
    
    public Factory() { }
        
    
    public Factory(Peer p) {
        this.setPeer(p);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer p) {
        this.peer = p;
    }
}
