package com.example.kadir.agricultureprojectsupportside;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.example.kadir.agricultureprojectsupportside.Interfaces.Callback;
import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.example.kadir.agricultureprojectsupportside.ShortCut.User;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.kadir.agricultureprojectsupportside.UserPageActivity.AdminFlag;

public class RegisterPageActivity extends AppCompatActivity implements LoginPageFragment.OnFragmentInteractionListener {

    private String email;
    private String password;
    private String clientName;
    private String clientSurname;
    private String clientCity;
    private String clientPhone;

    private FirebaseAuth mAuth;
    private FirebaseHelper firebaseHelper;
    @BindView(R.id.clientName) EditText mClientName;
    @BindView(R.id.clientSurname) EditText mClientSurname;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.clientCity) EditText mClientCity;
    @BindView(R.id.clientPhone) EditText mClientPhone;
    @OnClick(R.id.createBtn)
    void createButonClicked(){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        clientName = mClientName.getText().toString();
        clientSurname = mClientSurname.getText().toString();
        clientCity = mClientCity.getText().toString();
        clientPhone = mClientPhone.getText().toString();
        if (TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(clientName)
                || TextUtils.isEmpty(clientSurname)
                || TextUtils.isEmpty(clientCity)
                || TextUtils.isEmpty(clientPhone))
        {
            ShortCut.displayMessageToast(this, "You should fill empty fields!");

        }else {
            firebaseHelper.createNewUser(RegisterPageActivity.this, email, password, new Callback() {
                @Override
                public void callback() {
                    accountCreated();
                }
            });
        }
    }   

    public void accountCreated(){
        User user = new User(clientName, clientSurname, email, clientCity, clientPhone);
        firebaseHelper.putProfileInfo(user, MainActivity.MAuth2.getUid().toString());
        MainActivity.MAuth2.signOut();
        Intent intent = new Intent(this, EditUserActivity.class);
        startActivity(intent);
        this.finish();
    }


    public void loadLoginPageFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, new LoginPageFragment());
        ft.addToBackStack(null);
        ft.setTransition(50000);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        Intent goMain = new Intent(this, UserPageActivity.class);
        startActivity(goMain);
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_main);

        ButterKnife.bind(this);
        firebaseHelper = new FirebaseHelper();
        mAuth = firebaseHelper.getmFirebaseAuth();

       // firebaseHelper.checkUserLogin(RegisterPageActivity.this);
       // mAuthn = FirebaseAuth.getInstance();


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
