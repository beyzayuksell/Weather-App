package com.example.choosingtable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.choosingtable.model.tables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    /* Spinner ile yapmak daha mantıklı.
     Database'deki Table status(table durumu) boş olanları spinner a çekeriz.)
     */
    Button btn, btn2;
    TextView t; //en son silicem
    ArrayList<String> tableStatuslistFromDB = new ArrayList<String>();
    ArrayList<String> getStatuslistFromDB = new ArrayList<String>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tables");   // Hep aynı kalmasını istiyorsak databaseden verileri çekmesi lazım

    String controlBlankBtn1 = "true"; // ilk boşsa
    String controlBlankBtn2 = "true"; // ilk boşsa
    String controlBlankBtn3 = "true"; // ilk boşsa

    void init(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot values : snapshot.getChildren()) {
                    String value = values.child("status").getValue(String.class);
                    tableStatuslistFromDB.add(value);

                    Toast.makeText(MainActivity.this, "F:"+value, Toast.LENGTH_SHORT).show();
                }
                transmitArrayList(tableStatuslistFromDB);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    