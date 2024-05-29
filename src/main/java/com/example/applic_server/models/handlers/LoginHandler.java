package com.example.applic_server.models.handlers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class LoginHandler implements Runnable{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket login_socket;
    private Boolean logged_in = false;
    private HashMap<String, String> passwords = new HashMap<>();
    private HashMap<String, String> users = new HashMap<>();
    private HashMap<String, String> phones = new HashMap<>();
    private HashMap<String, String> emails = new HashMap<>();
    private ServerSocket serverSocket;
    JsonHandler js_handler = new JsonHandler();



    public LoginHandler(Socket login_socket, ObjectInputStream ois, ObjectOutputStream oos, ServerSocket serverSocket){
        this.ois = ois;
        this.oos = oos;
        this.serverSocket = serverSocket;
        this.login_socket = login_socket;
    }

    @Override
    public void run(){
        try {
            ois = new ObjectInputStream(login_socket.getInputStream());
            oos = new ObjectOutputStream(login_socket.getOutputStream());
            String user_type = "Error";
        while (!logged_in)
            {
                String user_desire = (String) ois.readObject();
                String name = (String) ois.readObject();
                user_type = (String) ois.readObject();
                String password = (String) ois.readObject();
                switch (user_desire) {
                    case "Login":
                        ois.readObject();
                        ois.readObject();
                        login_user(name, user_type, password, oos);
                        break;
                    case "Signup":
                        String phone = (String) ois.readObject();
                        String email = (String) ois.readObject();
                        signup_user(name, user_type, password, phone, email, oos);
                        break;
                }
            }
                Thread thread = new Thread();
                switch (user_type) { case "Client":
                        ClientHandler clientHandler = new ClientHandler(login_socket, ois, oos);
                        thread = new Thread(clientHandler);
                        break;
                    case "Admin":
                        AdminHandler adminHandler = new AdminHandler(login_socket, ois, oos);
                        thread = new Thread(adminHandler);
                        break;
                    case "Cleaner":
                        CleanerHandler cleanerHandler = new CleanerHandler(login_socket, ois, oos);
                        thread = new Thread(cleanerHandler);
                        break;
                }

                thread.start();
                Thread.currentThread().join();
            }
            catch (IOException | ClassNotFoundException | InterruptedException e){
            System.out.println("Fatal");
            }
    }


    public void login_user(String name, String user_type, String password, ObjectOutputStream oos) throws IOException {
        passwords = js_handler.load_json_passwords();
        users = js_handler.load_json_users();
        if (passwords.containsKey(name) && users.get(name).equals(user_type)) {
//            if (BCrypt.checkpw(password, passwords.get(name))) {
            if (password.equals(passwords.get(name))) {
                logged_in = true;
                oos.writeObject("Correct");
            } else {
                oos.writeObject("Incorrect");
            }
        }
        else{
            oos.writeObject("Incorrect");
        }
    }

    public void signup_user(String name, String user_type, String password, String phone, String email, ObjectOutputStream oos) throws IOException{
//        passwords.put("Admin", BCrypt.hashpw("Admin", BCrypt.gensalt()));
//        passwords.put("Admin", "Admin");
//        users.put("Admin", "Admin");
//        js_handler.store_json_passswords(passwords, users);
        passwords = js_handler.load_json_passwords();
        users = js_handler.load_json_users();
        phones = js_handler.load_json_phones();
        emails = js_handler.load_json_emails();
        if (passwords.containsKey(name)){
            oos.writeObject("Exist");
        }
        else{
            users.put(name, user_type);
//            passwords.put(name, BCrypt.hashpw(password, BCrypt.gensalt()));
            passwords.put(name, password);
            phones.put(name, phone);
            if (!Objects.equals(email, "")) {
                emails.put(name, email);
            }
            js_handler.store_json_passswords(passwords, users, phones, emails);
            oos.writeObject("Created");
        }
    }

    public void close_everything(Socket socket, ObjectOutputStream oos, ObjectInputStream ois){
        try {
            socket.close();
            oos.close();
            ois.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
