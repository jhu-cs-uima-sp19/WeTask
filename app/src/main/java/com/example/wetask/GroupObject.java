package com.example.wetask;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    private String groupID;
    private ArrayList<String> groupUserList = new ArrayList<String>();
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
    public ArrayList<String> getGroupUserList() {return groupUserList;}

    public void addGroupTask(String taskID) {
        this.groupTaskList.add(taskID);
    }
    public void removeGroupTask(String taskID){
        for(int i = 0; i < groupTaskList.size(); i++){
            if(groupTaskList.get(i).equals(taskID)){
                groupTaskList.remove(i);
            }
        }
    }

    public void addUser(String userID) {
        this.groupUserList.add(userID);
    }
}
