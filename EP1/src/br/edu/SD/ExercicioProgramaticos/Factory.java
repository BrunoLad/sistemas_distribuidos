/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.SD.ExercicioProgramaticos;

import java.io.*;

/**
 *
 * @author bruno
 */
// Classe para encapsular o objeto de Peer e enviar pela rede
public class Factory implements Serializable {

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

    private void setPeer(Peer p) {
        this.peer = p;
    }
}
