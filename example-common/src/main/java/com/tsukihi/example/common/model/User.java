package com.tsukihi.example.common.model;

import java.io.Serializable;

/**
 * 用户
 */

public class User implements Serializable{
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String _name){
        this.name = _name;
    }
}
