package com.example.wetask;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String name= "default name";
    private String assignedBy = " ";
    private String assignedTo = " ";
    private String deadline = " ";
    private String comments = " ";
    private String groupID = " ";
    private String taskId;
    private boolean finished;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    private Date date = new Date();
    private String createdDate = formatter.format(date);

    TaskItem(String item_name, String tID, String gID,
             String assignedBy, String assignedTo, String deadline, String comments) {
        this.name = item_name;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date date = new Date();
        this.createdDate = formatter.format(date);
        this.taskId = tID;
        this.groupID = gID;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.deadline = deadline;
        this.comments = comments;
        this.finished = false;
    }

    TaskItem() {
        //necessary for Firebase--throws exception if you don't have one
    }

    public String getCreatedDate() { return createdDate; }
    public String getDeadline() { return deadline; }
    public String getComments() { return comments; }
    public String getTaskId() {return taskId; }
    public String getName() {return name;}
    public String getAssignedBy() {return assignedBy;}
    public String getAssignedTo() {return assignedTo;}
    public Date getDate(){return date;}

    public void setDeadline(String newDL) { this.deadline = newDL; }
    public void setComments(String newNotes) { this.comments = newNotes; }
    public void setTaskName(String newName) { this.name = newName;}
    public void setAssignedBy(String newAssignedBy) { this.assignedBy = newAssignedBy; }
    public void SetAssignedTo(String newAssignedTo) { this.assignedTo = newAssignedTo; }

    public void complete(){this.finished = true;}
    public boolean ifFinished(){return this.finished;}

}
