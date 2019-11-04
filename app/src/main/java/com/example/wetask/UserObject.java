package com.example.wetask;

import java.util.List;

public class UserObject {
    private String userID;
    private List<Integer> groupList;
    private List<Integer> userTaskList;
    private String password;

    UserObject(String id, List<Integer> groups, String password) {
        this.userID = id;
        this.groupList = groups;
        this.password = password;
    }
    public String getUserID() {return userID;}

    public void addUserTask(int taskID) {
        this.userTaskList.add(taskID);
    }
}
