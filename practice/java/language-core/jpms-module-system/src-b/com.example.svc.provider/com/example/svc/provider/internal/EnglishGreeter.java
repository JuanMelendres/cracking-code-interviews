package com.example.svc.provider.internal;

import com.example.svc.api.Greeter;

public class EnglishGreeter implements Greeter {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "!";
    }
}
