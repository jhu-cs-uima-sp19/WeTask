package com.example.wetask;

/**
 * Holds data for one Task
 */
public class TaskItem {
    private String where;
    private String when;
    private String who;
    private String notes;
    private float cost;
    private short paid;

    TaskItem() {
    }

    public String getWhere() { return where; }
    public String getWhen() { return when; }
    public String getWho() { return who; }
    public String getNotes() { return notes; }
    public float getCost() { return cost; }
    public short getPaid() { return paid; }

    public void setAll(String where, String when, String who, String notes, float cost, short paid) {
        this.where = where;
        this.when = when;
        this.who = who;
        this.notes = notes;
        this.cost = cost;
        this.paid = paid;
    }
}
