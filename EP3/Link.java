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
public class Link {
    private String origin;              // Endereco base de onde os outros foram extraidos
    private ArrayList<String> links;    // Repositório com links dos outros clientes

    public Link(){
        this.links = new ArrayList<>();
    }

    public Link(String address) {
        this.origin = address;
        this.links = new ArrayList<>();
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public ArrayList<String> getLinks() {
        return this.links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }
}