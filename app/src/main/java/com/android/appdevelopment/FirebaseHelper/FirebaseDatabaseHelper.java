package com.android.appdevelopment.FirebaseHelper;

import android.support.annotation.NonNull;

import com.android.appdevelopment.Model.Lessons;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper  {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private List<Lessons> lessonsList = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("classroom");
    }

    public void readLessons(){
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonsList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNodes : dataSnapshot.getChildren()){
                    keys.add(keyNodes.getKey());
                    Lessons lesson = keyNodes.getValue(Lessons.class);
                    lessonsList.add(lesson);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
