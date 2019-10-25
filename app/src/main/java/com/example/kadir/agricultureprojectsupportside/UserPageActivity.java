package com.example.kadir.agricultureprojectsupportside;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kadir.agricultureprojectsupportside.Unused.FragmentEditUserDialogue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPageActivity extends AppCompatActivity implements LoginPageFragment.OnFragmentInteractionListener {
    public static boolean AdminFlag = false;

    private TextView currentUserIdText;
    private TextView currentUserNameText;
    private TextView currentUserEmailText;
    private TextView currentUserPhoneText;
    private TextView currentUserAddressText;

    private Button createNewUserButton;
    private Button editUserButton;
    private Button buttonSignOut;
    private Button editProfileButton;
    private Button editAccountButton;

    private ImageView imageAsset;
    private Dialog editDialog;
    private ProgressBar progressBar;

    private FirebaseHelper firebaseHelper;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Staff");

    private String uid;

    public void initializeScreen() {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName = (dataSnapshot.child(uid).child("staffName").getValue() + "")
                        + " " + (dataSnapshot.child(uid).child("staffLastname").getValue() + "");
                currentUserIdText.setText(dataSnapshot.child(uid).getKey().toString());
                currentUserNameText.setText(fullName);
                currentUserEmailText.setText(dataSnapshot.child(uid).child("staffEmail").getValue() + "");
                currentUserPhoneText.setText(dataSnapshot.child(uid).child("staffPhone").getValue() + "");
                currentUserAddressText.setText(dataSnapshot.child(uid).child("staffAddress").getValue() + "");
                progressBar.setVisibility(View.INVISIBLE);


                createNewUserButton.setEnabled(true);
                editUserButton.setEnabled(true);
                buttonSignOut.setEnabled(true);
                imageAsset.setEnabled(true);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(postListener);
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentEditUserDialogue editNameDialogFragment = FragmentEditUserDialogue.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_userpage);
        AdminFlag = true;
        uid = FirebaseHelper.getmFirebaseAuth().getUid();
        firebaseHelper = new FirebaseHelper();
        currentUserIdText = (TextView) findViewById(R.id.uidField);
        currentUserNameText = (TextView) findViewById(R.id.fullNameField);
        currentUserEmailText = (TextView) findViewById(R.id.mailField);
        currentUserPhoneText = (TextView) findViewById(R.id.phoneField);
        currentUserAddressText = (TextView) findViewById(R.id.adresField);
        createNewUserButton = (Button) findViewById(R.id.buttonCreateUser);
        editUserButton = (Button) findViewById(R.id.buttonEditUser);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        imageAsset = (ImageView)findViewById(R.id.edit_info_button);
        editDialog = new Dialog(UserPageActivity.this);
        editDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        createNewUserButton.setEnabled(false);
        editUserButton.setEnabled(false);
        buttonSignOut.setEnabled(false);
        imageAsset.setEnabled(false);
        while(uid == null){};
        this.initializeScreen();

        createNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                createNewUserButton.setEnabled(false);
                editUserButton.setEnabled(false);
                buttonSignOut.setEnabled(false);
                imageAsset.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                Intent goCreatePage = new Intent(UserPageActivity.this, RegisterPageActivity.class);
                startActivity(goCreatePage);
                finish();
            }
        });

        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                editUserButton.setEnabled(false);
                createNewUserButton.setEnabled(false);
                buttonSignOut.setEnabled(false);
                imageAsset.setEnabled(false);
                AdminFlag = true;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerUserPage, new LoginPageFragment());
                ft.addToBackStack(null);
                ft.setTransition(1);
                ft.commit();
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                buttonSignOut.setEnabled(false);
                createNewUserButton.setEnabled(false);
                editUserButton.setEnabled(false);
                imageAsset.setEnabled(false);
                firebaseHelper.signOut();
                AdminFlag = false;
                Intent goLoginPage = new Intent(UserPageActivity.this, MainActivity.class);
                startActivity(goLoginPage);
                finish();
            }
        });
        imageAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.setContentView(R.layout.edit_staff_popup);
                editAccountButton = (Button) editDialog.findViewById(R.id.change_pass_email);
                editProfileButton = (Button) editDialog.findViewById(R.id.change_profile_info);
                editProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editProfileButton.setEnabled(false);
                        editAccountButton.setEnabled(false);
                        editUserButton.setEnabled(false);
                        createNewUserButton.setEnabled(false);
                        buttonSignOut.setEnabled(false);
                        editDialog.dismiss();
                        Intent intent = new Intent(UserPageActivity.this , UpdateStaffProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                editAccountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editProfileButton.setEnabled(false);
                        editAccountButton.setEnabled(false);
                        editUserButton.setEnabled(false);
                        createNewUserButton.setEnabled(false);
                        buttonSignOut.setEnabled(false);
                        editDialog.dismiss();
                        Intent intent = new Intent(UserPageActivity.this , UpdateStaffAccountActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                editDialog.show();
            }
        });

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.INVISIBLE);
        AdminFlag = false;
        editUserButton.setEnabled(true);
        createNewUserButton.setEnabled(true);
        buttonSignOut.setEnabled(true);
        imageAsset.setEnabled(true);
        super.onBackPressed();
    }
}
