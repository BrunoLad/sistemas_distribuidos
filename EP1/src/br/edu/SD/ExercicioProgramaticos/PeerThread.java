/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.SD.ExercicioProgramaticos;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author bruno
 */
public class PeerThread extends Peer implements Runnable{
    String name;
    Thread t;
    
    public PeerThread(String thread){
        this.name = thread;
        t = new Thread(this, name);
        System.out.println("Nova thread: " + this.t);
        t.start();
    }
    
    @Override
    public void run(){
        try {
            while(true){
                switch (this.getName()) {
                    case "T0":
                        super.receiveStates();
                        break;
                    case "T1":
                        super.readFromFile();
                        Thread.sleep(2000);
                        break;
                    case "T2":
                        super.sendOwnState();
                        Thread.sleep(5000);
                        break;
                    case "T3":
                        super.sendOtherState();
                        Thread.sleep(7000);
                        break;
                    default:
                        super.deleteStates();
                        Thread.sleep(15000);
                        break;
                }
            }
        }
        catch(InterruptedException e){
            System.err.println(e);
            System.err.println(this.getName() + " Interrupted");
        }
         catch (SocketException e) {
            System.err.println(e);
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException | ClassNotFoundException | CloneNotSupportedException e) {
            System.err.println(e);
        }
        
        finally {
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
