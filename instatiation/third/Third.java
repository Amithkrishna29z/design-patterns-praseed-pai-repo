package instatiation.third;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class Third {
    public static void main(String[] args)
     throws IOException,
     ClassNotFoundException,
     InstantiationException,
     Exception {
        Class c = (Class)Class.forName("instatiation.third.Simple");

        if (c == null) {
            System.out.println("Failed to load the class....");
            return;
        }

        Constructor[] ctors = c.getDeclaredConstructors();
        Constructor ct = null;

        for(int i=0;i<ctors.length;i++) {
            ct=ctors[i];
            if(ct.getParameterTypes().length==0) break;
        }

        Simple s = (Simple)ct.newInstance();
        s.setMessage("Hello world");
        System.out.println(s.getMessage());       
    }   
}
