package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String name;
    private String createdDate;
    private String assignedBy;
    private String assignedTo;
    private String deadline;
    private String user;
    private String comments;
    private String groupID;
    private String taskId;
    private boolean finished;

    TaskItem(String item_name, String tID, String gID, String user) {
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
        this.user = user;
        this.createdDate = "no date"; //make this today (bc it's being created now)
        this.assignedBy = "no user found";
        this.assignedTo = "not assigned";
        this.deadline = "no deadline";
        this.comments = " ";
        this.finished = false;
    }

    TaskItem(String item_name, String tID, String gID, String user, String createdDate,
             String assignedBy, String assignedTo, String deadline, String comments) {
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
        this.user = user;
        this.createdDate = createdDate; //make this today (bc it's being created now)
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.deadline = deadline;
        this.comments = comments;
        this.finished = false;
    }

    TaskItem(){

    }

    public String getCreatedDate() { return createdDate; }
    public String getUser() { return user; }
    public String getDeadline() { return deadline; }
    public String getComments() { return comments; }
    public String getTaskId() {return taskId; }
    public String getName() {return name;}
    public void setDeadline(String newDL) { this.deadline = newDL; }
    public void setComments(String newNotes) { this.comments = newNotes; }
    public void setTaskName(String newName) { this.name = newName;}
    public void setFinished(){this.finished = true;}
    public boolean ifFinished(){return this.finished;}

}
