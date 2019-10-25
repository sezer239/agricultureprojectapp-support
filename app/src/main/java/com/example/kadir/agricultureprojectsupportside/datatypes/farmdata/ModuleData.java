package com.example.kadir.agricultureprojectsupportside.datatypes.farmdata;

import java.io.Serializable;

public class ModuleData implements Serializable {
    public String module_id;
    public String farm_name;
    public float soil_temperature;
    public float soil_humidity;
    public float air_temperature;
    public float air_humidity;
    public float ph;

    public boolean st_working;
    public boolean sh_working;
    public boolean at_working;
    public boolean ah_working;
    public boolean ph_working;

    public int count_of_working_sensors(){
        int count = 0;
        if(st_working) count++;
        if(sh_working) count++;
        if(at_working) count++;
        if(ah_working) count++;
        if(ph_working) count++;
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ModuleData){
            return ((ModuleData)obj).module_id == this.module_id;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( module_id + " {");
        sb.append(soil_temperature + "\n");
        sb.append(soil_humidity + "\n");
        sb.append(air_temperature + "\n");
        sb.append(air_humidity + "\n");
        sb.append(ph + "\n");
        sb.append("}");
        return sb.toString();
    }
}
