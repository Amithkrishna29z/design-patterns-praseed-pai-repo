package instatiation.second;

import java.beans.Beans;
import java.io.IOException;

public class Second {
    public static void main(String[] args) {
        try {
            Simple sm = (Simple)Beans.instantiate(Second.class.getClassLoader(), "instatiation.second.Simple");
            sm.setMessage("Hello world...");
            System.out.println(sm.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load the class....");
        }
    }
}
