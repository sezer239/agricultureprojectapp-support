package com.example.kadir.agricultureprojectsupportside;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Farm;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Module;
import com.example.kadir.agricultureprojectsupportside.module_selector.ModuleSelectorFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SezerMainActivity extends AppCompatActivity implements ModuleSelectorFragment.OnFragmentInteractionListener {

    TextView moduleId, airHum, airTemp, soilHum, soilTemp, ph;


    //EDIT TEXT
    EditText farm_size_edit_text;

    FarmEditView edit_farm;
    Farm farm;
    Button save_button;
    Button clean_button;
    Button undo_button;
    FragmentTransaction ft;

    Dialog dialogFarmName, dialogSensorInfo;
    ArrayList<String> farmNames;
    DatabaseReference ref;

    String user_id;
    String farmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sezer_main);

        edit_farm = findViewById(R.id.edit_view);
        undo_button = findViewById(R.id.undo_button);
        clean_button = findViewById(R.id.clean_button);
        save_button = findViewById(R.id.save_button);


        farmNames = new ArrayList<>();

        farm_size_edit_text = findViewById(R.id.famr_size_edit_text);

        farm_size_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) return;

                int kac_donum = Integer.parseInt(s.toString());
                int sqt_kac_donum = (int) Math.ceil(Math.sqrt(kac_donum));

                if (sqt_kac_donum <= 2) sqt_kac_donum = 2;
                if (sqt_kac_donum > 40) sqt_kac_donum = 40;
                edit_farm.change_farm_size(sqt_kac_donum);
            }
        });

        edit_farm.on_touch_down = new FarmEditView.OnTouchDown() {
            @Override
            public void on_touch_down() {
                save_button.setEnabled(false);
                undo_button.setEnabled(false);
                clean_button.setEnabled(false);
                farm_size_edit_text.setEnabled(false);
            }
        };

        edit_farm.on_touch_up = new FarmEditView.OnTouchUp() {
            @Override
            public void on_touch_up() {
                farm_size_edit_text.setEnabled(true);
                save_button.setEnabled(true);
                undo_button.setEnabled(true);
                clean_button.setEnabled(true);
            }
        };

        edit_farm.on_phase_change_listener = new FarmEditView.OnPhaseChange() {
            @Override
            public void on_phase_change(FarmEditView.EditPhases prev_phase, FarmEditView.EditPhases current_phase) {
                if (current_phase == FarmEditView.EditPhases.OUTLINE_DRAW_PHASE) {
                    farm_size_edit_text.setEnabled(true);
                    undo_button.setEnabled(true);
                } else if (current_phase == FarmEditView.EditPhases.MODULE_PLACEMENT_PHASE) {
                    farm_size_edit_text.setEnabled(false);
                    undo_button.setEnabled(false);
                }
            }
        };

        edit_farm.on_long_click_listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("ASD", "HEREE");
                if (edit_farm.current_phase() == FarmEditView.EditPhases.MODULE_PLACEMENT_PHASE
                        && edit_farm.allow_putting_data()) {
                    spawn_module_selection_fragment();
                } else if (edit_farm.current_phase() == FarmEditView.EditPhases.MODULE_PLACEMENT_PHASE && edit_farm.can_remove_data()) {
                    Module m = edit_farm.remove_data_last_selected_place();
                    if (m != null) {
                        ShortCut.displayMessageToast(SezerMainActivity.this, m.module_id + " Is Removed succsessfully");
                    }
                }
                return true;
            }
        };


        edit_farm.on_module_click_listener = new FarmEditView.OnModuleClick() {
            @Override
            public void on_module_click(String module_id) {
                ShowSensorInfoPopup(module_id);
            }
        };

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_farm.save_farm();
                if (edit_farm.current_phase() != FarmEditView.EditPhases.OUTLINE_DRAW_PHASE) {
                    FirebaseHelper.putFarmInfo(FirebaseHelper.getmEditingUserId(), farm, farm.farm_id);
                    Intent intent = new Intent(SezerMainActivity.this, EditUserActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        clean_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_farm.clean();
            }
        });

        undo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_farm.undo();
            }
        });

        Bundle extras = getIntent().getExtras();
        user_id = extras.getString("userID");
        farm = (Farm) extras.getSerializable("Farm");
//TODO: Edituser dan farmname i almak için eklendi.Kadir.
        farmName = extras.getString("farmName");

//TODO: popup kaldırıldığı için popup initialize kaldırıldı.Kadir.
        if (farm == null) {
            Log.v("www", farmName);
            farm = new Farm();
            farm.size = 16;

        }

        edit_farm.load_farm(farm);
        //TODO: recyclerview yerine buradan gönderildi farm ismi. Kadir.
        if (farmName != null) {
            edit_farm.edited_farm().farm_id = farmName;
        }
        farm_size_edit_text.setText(farm.size * farm.size + "");
    }

    public void spawn_module_selection_fragment() {
        ft = getSupportFragmentManager().beginTransaction();
        ModuleSelectorFragment module_selector_fragment = new ModuleSelectorFragment();

        module_selector_fragment.setOn_item_selected_callback(new ModuleSelectorFragment.OnSelectedItemCallback() {
            @Override
            public void on_selected_item_callback(String moduleData) {
                edit_farm.put_data_onto_last_selected_place(moduleData);
                ft = null;
            }
        });

        module_selector_fragment.load_module_data(edit_farm.getModule_data(), farm.farm_id);

        ft.replace(R.id.container, module_selector_fragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        if (ft == null) {
            Intent intent = new Intent(SezerMainActivity.this, EditUserActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            ft.addToBackStack(null);
            ft = null;
        }
        super.onBackPressed();
    }

    private void ShowSensorInfoPopup(final String module_Id) {
        dialogSensorInfo = new Dialog(SezerMainActivity.this);
        dialogSensorInfo.setContentView(R.layout.sensor_info_popup);
        moduleId = dialogSensorInfo.findViewById(R.id.moduleId);
        airHum = dialogSensorInfo.findViewById(R.id.air_hum);
        airTemp = dialogSensorInfo.findViewById(R.id.air_temp);
        soilHum = dialogSensorInfo.findViewById(R.id.soil_hum);
        soilTemp = dialogSensorInfo.findViewById(R.id.soil_temp);
        ph = dialogSensorInfo.findViewById(R.id.ph);

        ref = FirebaseDatabase.getInstance().getReference("User").child(user_id)
                .child("Modules").child(farm.farm_id).child("ModuleOfFarms");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if ((d.child("moduleId").getValue() + "").equals(module_Id)) {
                        moduleId.setText("Modul No : " + module_Id);
                        airHum.setText("Hava Nem: " + ((d.child("ahWorking").getValue() + "").equals("true") ? d.child("airHumidity").getValue() : "Çalışmıyor"));
                        airTemp.setText("Hava Sıcaklık: " + ((d.child("atWorking").getValue() + "").equals("true") ? d.child("airTemperature").getValue() : "Çalışmıyor"));
                        soilHum.setText("Toprak Nem : " + ((d.child("shWorking").getValue() + "").equals("true") ? d.child("soilHumidity").getValue() : "Çalışmıyor"));
                        soilTemp.setText("Toprak Sıcaklık : " + ((d.child("stWorking").getValue() + "").equals("true") ? d.child("soilTemperature").getValue() : "Çalışmıyor"));
                        ph.setText("ph: " + ((d.child("phWorking").getValue() + "").equals("true") ? d.child("ph").getValue() : "Çalışmıyor"));
                        dialogSensorInfo.show();
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//TODO: popup da değişiklik yapıldı. Seçim yapmaya zorlama eklendi, style değiştirildi. Kadir.


    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}