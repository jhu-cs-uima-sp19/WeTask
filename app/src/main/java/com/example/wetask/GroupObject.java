package com.example.wetask;

import java.util.List;

public class GroupObject {
    private String groupID;
    private List<String> userList;
    private List<Integer> groupTaskList;
    private String groupName;

    GroupObject(String id, List<String> users, String name) {
        this.groupID = id;
        this.userList = users;
        this.groupName = name;
    }

    public String getGroupID() {return groupID;}
    public String getGroupName() {return groupName;}

    public void addGroupTask(int taskID) {
        this.groupTaskList.add(taskID);
    }
}
