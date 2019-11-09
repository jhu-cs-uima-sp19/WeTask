package com.example.wetask;

import java.util.ArrayList;
import java.util.List;

public class UserObject {
    private String userID;
    private List<String> groupList;
    private String password;

    UserObject(){
        this.userID = "nameHolder";
        this.groupList = new ArrayList<String>();
        this.password = "passwordHolder";
    }

    UserObject(String id, List<String> groups, String password) {
        this.userID = id;
        this.groupList = groups;
        this.password = password;
    }
    public String getUserID() {return userID;}
    public List<String> getGroupList() {return groupList;}
    public String getPassword(){return password;}
}
