package com.example.kadir.agricultureprojectsupportside;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateStaffProfileActivity extends AppCompatActivity {

    //BUTTONS
    private Button cancel_button;
    private Button confirm_button;

    //EDIT TEXT FIELDS
    private EditText fname_edit_text;
    private EditText lname_edit_text;
    private EditText phone_edit_text;
    private EditText addr_edit_text;

    //PROGRES BARR
    private ProgressBar progress_bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff_profile);

        //BUTTON INIT
        cancel_button = findViewById(R.id.buttonCancel);
        confirm_button = findViewById(R.id.buttonUpdate);

        //EDITTEXT INIT
        addr_edit_text = findViewById(R.id.addr_field_edit_text);
        phone_edit_text = findViewById(R.id.phone_field_edit_text);
        fname_edit_text = findViewById(R.id.fname_field_edit_text);
        lname_edit_text = findViewById(R.id.lname_field_edit_text);

        //PROGRES BARR
        progress_bar = findViewById(R.id.progress_bar_edituser_profile);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Staff").child(FirebaseAuth.getInstance().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fname_edit_text.setText(dataSnapshot.child("staffName").getValue() + "");
                lname_edit_text.setText(dataSnapshot.child("staffLastname").getValue() + "");
                phone_edit_text.setText(dataSnapshot.child("staffPhone").getValue() + "");
                addr_edit_text.setText(dataSnapshot.child("staffAddress").getValue() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_button.setEnabled(false);
                cancel_button.setEnabled(false);
                progress_bar.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Staff").child(FirebaseAuth.getInstance().getUid());

                String fname = fname_edit_text.getText().toString();
                String lname = lname_edit_text.getText().toString();
                String addr  = addr_edit_text. getText().toString();
                String phone = phone_edit_text.getText().toString();

                if (fname.isEmpty() || lname.isEmpty() || addr.isEmpty() || phone.isEmpty()) {
                    ShortCut.displayMessageToast(UpdateStaffProfileActivity.this , "Fill all fields!");
                    progress_bar.setVisibility(View.INVISIBLE);
                    confirm_button.setEnabled(true);
                    cancel_button.setEnabled(true);
                } else {
                    ref.child("staffName").setValue(fname);
                    ref.child("staffLastname").setValue(lname);
                    ref.child("staffAddress").setValue(addr);
                    ref.child("staffPhone").setValue(phone);
                    Intent intent = new Intent(UpdateStaffProfileActivity.this , UserPageActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_button.setEnabled(false);
                confirm_button.setEnabled(false);
                Intent intent = new Intent(UpdateStaffProfileActivity.this , UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UpdateStaffProfileActivity.this, UserPageActivity.class);
        startActivity(intent);
        finish();
    }
}
