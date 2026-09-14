package com.example.modB;

import com.example.modA.Secret;
import java.lang.reflect.Field;

public class ReflectiveProbe {
    public static void main(String[] args) throws Exception {
        Secret s = new Secret();
        Field f = Secret.class.getDeclaredField("value");
        f.setAccessible(true);
        System.out.println("read private field via reflection: " + f.get(s));
    }
}
