package com.mycompany.app;

/**
 * Hello world!
 */
public class App
{

    private final String message = "Hello World!";

    public App() {}

    public static void main(String[] args) {
        String mess = getMessage();
        mess += "add some text";
        System.out.println(mess);
    }

    private final String getMessage() {
        return message;
    }

}
