package com.example.kadir.agricultureprojectsupportside.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kadir.agricultureprojectsupportside.FarmEditView;
import com.example.kadir.agricultureprojectsupportside.FirebaseHelper;
import com.example.kadir.agricultureprojectsupportside.Interfaces.OnGetFarmCallback;
import com.example.kadir.agricultureprojectsupportside.R;
import com.example.kadir.agricultureprojectsupportside.SezerMainActivity;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Farm;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<FarmEditView> farmOutlineView;
    LayoutInflater layoutInflater;
    Context context;
    String currentUserId;
    Dialog dialog;
    Farm farm;

    public CustomAdapter(ArrayList<FarmEditView> farmOutlineView, Context context, String currentUserId) {
        this.context = context;
        this.farmOutlineView=farmOutlineView;
        this.currentUserId = currentUserId;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.edit_user_page_recycler_view_row, viewGroup, false);
        ViewHolder view_holder = new ViewHolder(v);

        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            viewHolder.linearLayout.addView(farmOutlineView.get(i));
        viewHolder.textView.setText(farmOutlineView.get(i).edited_farm().farm_id + "");
            farmOutlineView.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("CCC", "Onclick çalışıyor");
                    FirebaseHelper.getUserAllFarmSingle(currentUserId, new OnGetFarmCallback() {
                        @Override
                        public void onGetFarmCallback(ArrayList<Farm> farmdata) {
                            farm = farmdata.get(i);
                            Bundle extras = new Bundle();
                            extras.putSerializable("Farm", farm);
                            extras.putString("userID", currentUserId);
                            Intent intent = new Intent(context, SezerMainActivity.class);
                            intent.putExtras(extras);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    });

                }
            });
            farmOutlineView.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final TextView farmNameTxt, latTxt, lonTxt;
                    final String farm_id = farmOutlineView.get(i).edited_farm().farm_id;
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.farm_location);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    farmNameTxt = (TextView) dialog.findViewById(R.id.farmName);
                    latTxt =      (TextView) dialog.findViewById(R.id.latitude);
                    lonTxt =      (TextView) dialog.findViewById(R.id.longtitude);
//                    while(farm_id == null);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("FarmsLocations");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            farmNameTxt.setText(farm_id + "");
                            latTxt.setText("lat : " + dataSnapshot.child(farm_id +"").child("location").child("lat").getValue() + "");
                            lonTxt.setText("lon : " + dataSnapshot.child(farm_id +"").child("location").child("lon").getValue() + "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dialog.show();
                    return true;
                }
            });
    }

    public FarmEditView getFarmAt(int position){
        return farmOutlineView.get(position);
    }

    @Override
    public int getItemCount() {
            return farmOutlineView.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout linearLayout;
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear_recyclerview_rows);
            textView = itemView.findViewById(R.id.farm_name_txt);
        }
    }


}
