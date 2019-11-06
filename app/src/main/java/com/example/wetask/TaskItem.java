package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String name = "Task";
    private String createdDate;
    private String deadline;
    private String user;
    private String notes;
    private String groupID;
    private String taskId;

    TaskItem(String item_name, String tID, String gID) { //Constructor currently empty, do stuff bout this
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
    }

    TaskItem(){

    }

    public String getCreatedDate() { return createdDate; }
    public String getUser() { return user; }
    public String getDeadline() { return deadline; }
    public String getNotes() { return notes; }
    public String getTaskId() {return taskId; }
    public String getTaskName() {return name;}
    public void setDeadline(String newDL) { this.deadline = newDL; }
    public void setNotes(String newNotes) { this.notes = newNotes; }
    public void setTaskName(String newName) { this.name = newName;}

}
