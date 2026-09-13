package com.example.svc.app;

import com.example.svc.api.Greeter;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<Greeter> loader = ServiceLoader.load(Greeter.class);
        int found = 0;
        for (Greeter g : loader) {
            found++;
            System.out.println(g.getClass().getName() + " -> " + g.greet("World"));
        }
        System.out.println("providers found: " + found);
    }
}
