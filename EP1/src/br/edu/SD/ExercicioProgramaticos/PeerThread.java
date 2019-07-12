/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.SD.ExercicioProgramaticos;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;

/**
 *
 * @author bruno
 */
public class PeerThread implements Runnable, Serializable {

    private String name;
    private Thread t;
    private Peer p;

    public Peer getP() {
        return p;
    }

    public void setP(Peer p) {
        this.p = p;
    }

    public PeerThread() {

    }

    public PeerThread(String thread) {
        this.name = thread;
        t = new Thread(this, name);
        System.out.println("Nova thread: " + this.t);
        t.start();
    }

    public PeerThread(String thread, Peer p) {
        this.p = p;
        this.name = thread;
        t = new Thread(this, name);
        System.out.println("Nova thread: " + this.t);
        t.start();
    }

    @Override
    public void run() {
        try {
            while (!this.getName().equals("T0") && true) {
                switch (this.getName()) {
                    //case "T0":
                    //    break;
                    case "T1":
                        System.out.println(this.getName() + " "
                                + this.getP().readFromFile());
                        Thread.sleep(2000);
                        break;
                    case "T2":
                        Thread.sleep(5000);
                        System.out.println(this.getName() + " "
                                + this.getP().sendOwnState());
                        break;
                    case "T3":
                        Thread.sleep(7000);
                        System.out.println(this.getName() + " "
                                + this.getP().sendOtherState());
                        break;
                    default:
                        Thread.sleep(10000);
                        this.getP().deleteStates();
                        break;
                }
            }
            this.getP().receiveStates();
            
        } catch (InterruptedException e) {
            System.err.println(e);
            System.err.println(this.getName() + " Interrupted");
        } catch (SocketException e) {
            System.err.println(e);
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException | ClassNotFoundException | CloneNotSupportedException e) {
            System.err.println(e);
        } finally {
            System.out.println(name + " exiting.");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
