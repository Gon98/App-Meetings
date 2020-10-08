package com.example.myproject;

public class User {
    String name;
    String email;

    //Construtor
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    //GET SET
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //String
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
