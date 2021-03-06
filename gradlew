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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = findViewById(R.id.editTextLists);

        btn = findViewById(R.id.tableBtn1);
        //btn = findViewById(R.id.tableBtn1); // buton id'si döner. btnID=tableBtn1 -> for table 1
        btn2 = findViewById(R.id.tableBtn2);


        controlBlankBtn1 = getStatuslistFromDB.get(0);
        controlBlankBtn2 = getStatuslistFromDB.get(1);
        controlBlankBtn3 = getStatuslistFromDB.get(2);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controlBlankBtn1.equals("true")) { // Boşsa seçilir, renk kırmızıya döner.
                    String tableNoClickFromPage = btn.getText().toString();
                    btn.setBackgroundColor(Color.parseColor("#FFF44336"));
                    updateDatabaseTableStatus(tableNoClickFromPage);
                } else { // Doluysa renk kırmızı kalır.
                    btn.setBackgroundColor(Color.parseColor("#FFF44336"));
                    Toast.makeText(MainActivity.this, "Filled!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateDatabaseTableStatus(String clickedTableNo) {
            tables tableObj = new tables(clickedTableNo);
            if(tableObj.isStatus()){ // true, chosed table is blank, convert red color
                databaseReference.child(clickedTableNo).child("status").setValue("false");
            }
            else{ // false, so, chosed table is filling
                //eğer database değer false uyarı mesajı verelim
                Toast.makeText(MainActivity.this, "This place is filled.", Toast.LENGTH_SHORT).show();
            }

    }

    public void transmitArrayList(ArrayList<String> list) {


        for (int i=0; i<3; i++) {
            getStatuslistFromDB.add(list.get(i));
        }
        getStatuslistFromDB = list;
        t.setText(getStatuslistFromDB.get(0)+"\n"+getStatuslistFromDB.get(1)+"\n"+getStatuslistFromDB.get(2));
    }


}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                