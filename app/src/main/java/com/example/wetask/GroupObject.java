package com.example.wetask;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    private String groupID;
    private ArrayList<String> userList = new ArrayList<String>();
    private ArrayList<String> groupTaskList = new ArrayList<String>();
    private String groupName;

    GroupObject(String id, String name) {
        this.groupID = id;
        this.groupName = name;
    }

    GroupObject(){

    }

    public String getGroupID() {return groupID;}
    public String getGroupName() {return groupName;}
    public ArrayList<String> getGroupTaskList() {return groupTaskList;}

    public void addGroupTask(String taskID) {
        this.groupTaskList.add(taskID);
    }

    public void addUser(String userID) {
        this.userList.add(userID);
    }
}
