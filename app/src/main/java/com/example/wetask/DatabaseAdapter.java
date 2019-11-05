package com.example.wetask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatabaseAdapter {
    private DatabaseReference mDatabase;

    DatabaseAdapter(){
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void add_user(UserObject usr){
        mDatabase.child("users").child(usr.getUserID()).setValue(usr);
        List<String> groupList = usr.getGroupList();
        for(int i = 0; i < groupList.size(); i++){
            String gp = groupList.get(i);
            mDatabase.child("groups").child(gp).child("userList");
        }
    }



}
