package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String name = "Task";
    private String where;
    private String when;
    private String who;
    private String notes;
    private float cost;
    private float paid;
    private String taskId;

    TaskItem(String item_name, String id) { //Constructor currently empty, do stuff bout this
        this.name = item_name;
        this.taskId = id;
    }

    TaskItem(){

    }

    public String getWhere() { return where; }
    public String getWhen() { return when; }
    public String getWho() { return who; }
    public String getNotes() { return notes; }
    public float getCost() { return cost; }
    public float getPaid() { return paid; }
    public String getTaskId() {return taskId; }
    public String getName() {return name;}

    public void setAll() {//Add stuff to set in the parameters

    }
}
