package com.example.kadir.agricultureprojectsupportside;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.example.kadir.agricultureprojectsupportside.Unused.FirstFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LoginPageFragment.OnFragmentInteractionListener {

    FirebaseHelper firebaseHelper;
    public static FirebaseAuth MAuth1, MAuth2;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isOnline()) {
            firebaseHelper = new FirebaseHelper();
            MAuth1 = FirebaseAuth.getInstance();
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://agriculture-project-c8a72.firebaseio.com/")
                    .setApiKey("AIzaSyAkWSgdIQA4vMrCrMCMuZDV7FQ_FdkIIlk")
                    .setApplicationId("agriculture-project-c8a72").build();
//            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                ShortCut.displayMessageToast(MainActivity.this, "asdasdasd");
//                return;
//            }
//
//            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            double longitude = location.getLongitude();
//            double latitude = location.getLatitude();
            try {
                FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
                MAuth2 = FirebaseAuth.getInstance(myApp);
            } catch (IllegalStateException e) {
                MAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
            }
            //  if(!UserPageActivity.AdminFlag) {
            if (firebaseHelper.getFirebaseUserAuthID() == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, new LoginPageFragment());
                //   ft.addToBackStack(null);
                ft.setTransition(1);
                ft.commit();
            } else {
                Log.v("ASD", firebaseHelper.getFirebaseUserAuthID());
                firebaseHelper.checkUserLogin(MainActivity.this);
                this.finish();
            }
        }else{
            Toast.makeText(MainActivity.this, "İnternet bağlantınızı kontrol ediniz",Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(2000);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}