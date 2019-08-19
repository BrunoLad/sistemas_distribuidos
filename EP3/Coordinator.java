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
public class Coordinator {
    private Mapper[] m;                         //Endereço e informacoes dos mappers
    
    public Coordinator() { }

    public Mapper[] getM() {
        return m;
    }

    public void setM(Mapper[] m) {
        this.m = m;
    }
    
    
}
