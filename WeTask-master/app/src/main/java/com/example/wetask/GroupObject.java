package com.example.wetask;

import java.util.List;

public class GroupObject {
    private int groupID;
    private List<String> userList;
    private String groupName;

    GroupObject(int id, List<String> users, String name) {
        this.groupID = id;
        this.userList = users;
        this.groupName = name;
    }

    public int getGroupID() {return groupID;}
    public String getGroupName() {return groupName;}
}