package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    //Temp Variables
    private String where;
    private String when;
    private String who;
    private String notes;
    private float cost;
    private short paid;

    TaskItem() { //Constructor currently empty, do stuff bout this
    }

    public String getWhere() { return where; }
    public String getWhen() { return when; }
    public String getWho() { return who; }
    public String getNotes() { return notes; }
    public float getCost() { return cost; }
    public short getPaid() { return paid; }

    public void setAll() {//Add stuff to set in the parameters

    }
}
