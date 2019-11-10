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
    private boolean finished = false;

    TaskItem(String item_name, String tID, String gID, String user) {
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
        this.user = user;
    }

    TaskItem(){

    }

    public String getCreatedDate() { return createdDate; }
    public String getUser() { return user; }
    public String getDeadline() { return deadline; }
    public String getNotes() { return notes; }
    public String getTaskId() {return taskId; }
    public String getName() {return name;}
    public void setDeadline(String newDL) { this.deadline = newDL; }
    public void setNotes(String newNotes) { this.notes = newNotes; }
    public void setTaskName(String newName) { this.name = newName;}
    public void setFinished(){this.finished = true;}
    public boolean ifFinished(){return this.finished;}

}
