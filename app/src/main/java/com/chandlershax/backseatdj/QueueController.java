package com.chandlershax.backseatdj;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class QueueController {


    public static void GetNextSong() {
        Log.d("QueueController","Test");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sessionsRef = database.child("queues/");
        DatabaseReference priRef = sessionsRef.child("-L9TSTpzYmb5L0DRzt0q").child("songs");
        Query nextSong = priRef.orderByKey().limitToFirst(1);
        nextSong.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("QueueController", "The database thingy worked");
                Log.d("QueueController", dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("QueueController", "Error encountered");

            }
        });
    }

    public static void TestQueueController() {

    }
}