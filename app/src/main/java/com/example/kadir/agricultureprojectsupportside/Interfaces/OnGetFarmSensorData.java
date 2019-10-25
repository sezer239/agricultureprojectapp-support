package com.example.kadir.agricultureprojectsupportside.Interfaces;


import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.ModuleData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface OnGetFarmSensorData {
    public void onFarmSensorData(ArrayList<ModuleData> moduleData);
}
