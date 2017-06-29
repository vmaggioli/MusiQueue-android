package com.example.musiqueue.HelperClasses;

public class User {
    public String id;
    public String name;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public User() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
