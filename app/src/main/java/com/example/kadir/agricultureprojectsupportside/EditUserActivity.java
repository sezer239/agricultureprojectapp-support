package com.example.kadir.agricultureprojectsupportside;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kadir.agricultureprojectsupportside.Adapter.CustomAdapter;
import com.example.kadir.agricultureprojectsupportside.Adapter.PopupRecyclerViewAdapter;
import com.example.kadir.agricultureprojectsupportside.Interfaces.OnGetFarmCallback;
import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.example.kadir.agricultureprojectsupportside.ShortCut.User;

import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Farm;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity{


    private ArrayList<Farm> UserFarmData;
    private User user = new User();
    private ArrayList<FarmEditView> mFarmCard;
    private FarmEditView farmoutlineview;
    private Toolbar toolbar;

    private String mCurrentEditUserId;
    private FirebaseHelper firebaseHelper;
    private String Name, LastName;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("User");

    private TextView mClientId, mClientFullName, mClientEmail, mClientPhone, mClientAddress;

    FloatingActionButton fab;
    ProgressBar locationProgressBar;

    RecyclerView farmRecyclerView;
    CustomAdapter customAdapter;
    RecyclerView popupRecyclerView;
    PopupRecyclerViewAdapter popupRecyclerViewAdapter;

    Dialog checkDialog;
    Dialog clientInfoDialog;
    Dialog dialogFarmName;
    Dialog checkSavingFarmLocationDialog;
    LocationListener locationListener;

    //GPS
    LocationManager locationManager;
    String provider;
    double lat, lon;
    Intent intentThatCalled;
    public Criteria criteria;
    private boolean flag;
    private MyLocationListener myLocationListener;
    private boolean permissionFlag;
    private boolean yesFlag = false;

    private HashMap<DatabaseReference, ValueEventListener> hashMap = new HashMap<>();
    private ValueEventListener mFarmNameListener;
    private Dialog locWaitingDialog;
    private Button cancelButton;


    public void updateCurrentUserData() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = mCurrentEditUserId;
                Name = dataSnapshot.child(uid).child("profile").child("userName").getValue() + "";
                LastName = dataSnapshot.child(uid).child("profile").child("userLastname").getValue() + "";
                String email = dataSnapshot.child(uid).child("profile").child("userEmail").getValue() + "";
                String phone = dataSnapshot.child(uid).child("profile").child("userPhone").getValue() + "";
                String address = dataSnapshot.child(uid).child("profile").child("userAddress").getValue() + "";
                user.setUserId(uid);
                user.setUserName(Name);
                user.setUserLastname(LastName);
                user.setUserEmail(email);
                user.setUserPhone(phone);
                user.setUserAddress(address);
                setTitle(Name + " " + LastName);
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clientInfoDialog = new Dialog(EditUserActivity.this);
                        clientInfoDialog.setContentView(R.layout.client_info_popup);
                        clientInfoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        mClientId = clientInfoDialog.findViewById(R.id.clientIdField);
                        mClientFullName = clientInfoDialog.findViewById(R.id.clientFullNameField);
                        mClientEmail = clientInfoDialog.findViewById(R.id.clientMailField);
                        mClientPhone = clientInfoDialog.findViewById(R.id.clientPhoneField);
                        mClientAddress = clientInfoDialog.findViewById(R.id.clientAdresField);

                        mClientId.setText(user.getUserId() + "");
                        mClientFullName.setText(Name + " " + LastName);
                        mClientEmail.setText(user.getUserEmail() + "");
                        mClientPhone.setText(user.getUserPhone() + "");
                        mClientAddress.setText(user.getUserAddress() + "");

                        clientInfoDialog.show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentEditUserId = FirebaseHelper.getmEditingUserId();
        mFarmCard = new ArrayList<FarmEditView>();
        UserFarmData = new ArrayList<Farm>();
        farmRecyclerView = (RecyclerView) findViewById(R.id.farm_image_recyclerview);
        LinearLayoutManager lm = new LinearLayoutManager(EditUserActivity.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        lm.scrollToPosition(0);
        farmRecyclerView.setLayoutManager(lm);
        farmRecyclerView.setHasFixedSize(true);
        farmRecyclerView.setLayoutManager(lm);

//        checkLocationPermission();
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(location_context);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);

        FirebaseHelper.getUserAllFarm(mCurrentEditUserId, new OnGetFarmCallback() {
            @Override
            public void onGetFarmCallback(ArrayList<Farm> farmdata) { ////Canlı görüntü sağlayabilmek için interface kullandık
                initializeFarmImage(farmdata);
                fab.setEnabled(true);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ShowCheckPopup(viewHolder);
            }
        }).attachToRecyclerView(farmRecyclerView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSavingFarmLocationDialog = new Dialog(EditUserActivity.this);
                saveLocationPopup();
            }
        });
    }

    void initializeFarmImage(ArrayList<Farm> farmdata) {
        Log.v("ASD", "farmdata size " + farmdata.size());
        mFarmCard.clear();
        for (final Farm f : farmdata) {                          /////eğer kullanmasaydık data canlı kalırdı fakat resimler bir defa oluşturulurdu
            FarmEditView initfarmCard = new FarmEditView(EditUserActivity.this);
            initfarmCard.load_farm(f, true);
            initfarmCard.setMinimumWidth(1000);
            initfarmCard.setMinimumHeight(1000);
            initfarmCard.setMaxWidth(1000);
            initfarmCard.setMaxHeight(1000);
            mFarmCard.add(initfarmCard);
        }
        customAdapter = new CustomAdapter(mFarmCard, EditUserActivity.this, mCurrentEditUserId);
        farmRecyclerView.setAdapter(customAdapter);

        updateCurrentUserData();
    }


    public void ShowCheckPopup(final RecyclerView.ViewHolder viewHolder) {
        Button checkButton, rejectButton;
        checkDialog = new Dialog(EditUserActivity.this);
        checkDialog.setContentView(R.layout.remove_popup);
        checkDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        checkDialog.setCancelable(false);
        checkButton = (Button) checkDialog.findViewById(R.id.check_button);
        rejectButton = (Button) checkDialog.findViewById(R.id.reject_button);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(mCurrentEditUserId).child("Farms").child((customAdapter.getFarmAt(viewHolder.getAdapterPosition()))
                        .edited_farm().farm_id).removeValue();
                ref.child(mCurrentEditUserId).child("FarmsLocations").child((customAdapter.getFarmAt(viewHolder.getAdapterPosition()))
                        .edited_farm().farm_id).removeValue();
                checkDialog.dismiss();
                Toast.makeText(EditUserActivity.this, "Farm deleted", Toast.LENGTH_SHORT).show();
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditUserActivity.this, EditUserActivity.class);
                startActivity(intent);
                finish();
                checkDialog.dismiss();
            }
        });
        checkDialog.show();
    }


    private void ShowPopup() {

        dialogFarmName.setContentView(R.layout.popup_recyclerview);
        popupRecyclerView = (RecyclerView) dialogFarmName.findViewById(R.id.popup_rec_view);
        dialogFarmName.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        fab.setEnabled(true);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        lm.scrollToPosition(0);
        popupRecyclerView.setHasFixedSize(true);
        popupRecyclerView.setLayoutManager(lm);
        dialogFarmName.setCancelable(false);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(mCurrentEditUserId);
        mFarmNameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> fnameList = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.child("Modules").getChildren()) {
                    String a = d.getKey() + "";
                    fnameList.add(a);
                }
                for (DataSnapshot d : dataSnapshot.child("Farms").getChildren()) {
                    if (fnameList.contains(d.getKey() + "")) {
                        fnameList.remove(d.getKey() + "");
                    }
                }
                if (fnameList.size() == 0) {
                    ShortCut.displayMessageToast(EditUserActivity.this, "No availible farm to create");
                    dialogFarmName.dismiss();
                }

                popupRecyclerViewAdapter = new PopupRecyclerViewAdapter(EditUserActivity.this, fnameList, new EditUserActivity.OnClickFarmName() {
                    @Override
                    public void onClickFarmName(String farmName) {
                        popupRecyclerView.setEnabled(false);
                        if(yesFlag == true){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(mCurrentEditUserId).child("FarmsLocations");
                            ref.child(farmName).child("location").child("lon").setValue(lon);
                            ref.child(farmName).child("location").child("lat").setValue(lat);
                        }
                        Intent intent = new Intent(EditUserActivity.this, SezerMainActivity.class);
                        intent.putExtra("userID", mCurrentEditUserId);
                        intent.putExtra("farmName", farmName);
                        startActivity(intent);
                        dialogFarmName.dismiss();
                        reference.removeEventListener(mFarmNameListener);
                        finish();
                    }
                });
                popupRecyclerView.setAdapter(popupRecyclerViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(mFarmNameListener);
        dialogFarmName.show();


    }

    private void saveLocationPopup() {

        final Button yesButton, noButton;
        TextView txt;

        checkSavingFarmLocationDialog.setContentView(R.layout.check_saving_loc_popup);
        checkSavingFarmLocationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        yesButton = (Button) checkSavingFarmLocationDialog.findViewById(R.id.yes_button);
        noButton = (Button) checkSavingFarmLocationDialog.findViewById(R.id.no_button);
        txt = (TextView) checkSavingFarmLocationDialog.findViewById(R.id.save_location_txt);
        txt.setText("  Do you want to create a farm in this location ?");
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesButton.setEnabled(false);
                noButton.setEnabled(false);
                yesFlag = true;
                if(permissionFlag == true){
                    find_Location();
                    checkSavingFarmLocationDialog.dismiss();
                }else{
                    checkLocationPermission();
                    checkSavingFarmLocationDialog.dismiss();
                }
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noButton.setEnabled(false);
                yesButton.setEnabled(false);
                checkSavingFarmLocationDialog.dismiss();
                dialogFarmName = new Dialog(EditUserActivity.this);
                ShowPopup();
            }
        });
        checkSavingFarmLocationDialog.show();
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                ActivityCompat.requestPermissions(EditUserActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            find_Location();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
//                        find_Location();
                        permissionFlag = true;
                    }

                } else {
                    ShortCut.displayMessageToast(EditUserActivity.this, "Permission denied");

                }

            }
        }
    }

    public void find_Location() {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null && flag == false) {
                flag = true;
                myLocationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(provider, 100, 1, myLocationListener);

                locWaitingDialog = new Dialog(EditUserActivity.this);
                locWaitingDialog.setContentView(R.layout.location_waiting_popup);
                locWaitingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                locWaitingDialog.setCancelable(false);
                cancelButton = (Button) locWaitingDialog.findViewById(R.id.location_cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationManager.removeUpdates(myLocationListener);
                        locWaitingDialog.dismiss();
                    }
                });
                locWaitingDialog.show();
            }else if(flag == false){
                flag = true;
                myLocationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(provider, 100, 1, myLocationListener);

                locWaitingDialog = new Dialog(EditUserActivity.this);
                locWaitingDialog.setContentView(R.layout.location_waiting_popup);
                locWaitingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                locWaitingDialog.setCancelable(false);
                cancelButton = (Button) locWaitingDialog.findViewById(R.id.location_cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationManager.removeUpdates(myLocationListener);
                        locWaitingDialog.dismiss();
                    }
                });
                locWaitingDialog.show();
            }else{
                ShortCut.displayMessageToast(EditUserActivity.this, "Location not Available");

            }
    }

    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            ShortCut.displayMessageToast(EditUserActivity.this, "Location captured");
            locationManager.removeUpdates(myLocationListener);
            locWaitingDialog.dismiss();
            fab.setEnabled(false);
            flag = false;
            dialogFarmName = new Dialog(EditUserActivity.this);
            ShowPopup();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            ShortCut.displayMessageToast(EditUserActivity.this, "Status Changed");

        }

        @Override
        public void onProviderEnabled(String provider) {
            ShortCut.displayMessageToast(EditUserActivity.this, "Enable");

        }

        @Override
        public void onProviderDisabled(String provider) {
            ShortCut.displayMessageToast(EditUserActivity.this, "Disable");

        }
    }

    public interface OnClickFarmName{
        public void onClickFarmName(String farmName);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditUserActivity.this, UserPageActivity.class);
        startActivity(intent);
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
