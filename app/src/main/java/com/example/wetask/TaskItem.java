package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String name= "default name";
    private String createdDate;
    private String assignedBy;
    private String assignedTo;
    private String deadline;
    private String comments;
    private String groupID;
    private String taskId;
    private boolean finished;

    TaskItem(String item_name, String tID, String gID, String assignedTo) {
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
        this.createdDate = "no date"; //make this today (bc it's being created now)
        this.assignedBy = "no user found";
        this.assignedTo = assignedTo;
        this.deadline = "no deadline";
        this.comments = " ";
        this.finished = false;
    }

    TaskItem(String item_name, String tID, String gID, String createdDate,
             String assignedBy, String assignedTo, String deadline, String comments) {
        this.name = item_name;
        this.taskId = tID;
        this.groupID = gID;
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
    public String getDeadline() { return deadline; }
    public String getComments() { return comments; }
    public String getTaskId() {return taskId; }
    public String getName() {return name;}
    public String getAssignedBy() {return assignedBy;}
    public String getAssignedTo() {return assignedTo;}

    public void setDeadline(String newDL) { this.deadline = newDL; }
    public void setComments(String newNotes) { this.comments = newNotes; }
    public void setTaskName(String newName) { this.name = newName;}
    public void setAssignedBy(String newAssignedBy) { this.assignedBy = newAssignedBy; }
    public void SetAssignedTo(String newAssignedTo) { this.assignedTo = newAssignedTo; }

    public void complete(){this.finished = true;}
    public boolean ifFinished(){return this.finished;}

}
