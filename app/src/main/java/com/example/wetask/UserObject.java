package com.example.wetask;

import java.util.ArrayList;
import java.util.List;

public class UserObject {
    private String userID;
    private ArrayList<String> groupList;
    private String password;
    private String lastGroupAccessed;

    UserObject(){
        this.userID = "nameHolder";
        this.groupList = new ArrayList<>();
        this.password = "passwordHolder";
    }

    UserObject(String id, ArrayList<String> groups, String password) {
        this.userID = id;
        this.groupList = groups;
        this.password = password;
    }
    public String getUserID() {return userID;}
    public ArrayList<String> getGroupList() {return groupList;}
    public String getPassword(){return password;}
    public void addGroup(String groupID){groupList.add(groupID);}
    public void removeGroup(String groupID){
        for(String g:this.groupList){
            if(g.equals(groupID)){
                groupList.remove(g);
                break;
            }
        }
    }
    public void setLastGroup(String last) {lastGroupAccessed = last;}
    public String getLastGroupAccessed() {return lastGroupAccessed;}
}
