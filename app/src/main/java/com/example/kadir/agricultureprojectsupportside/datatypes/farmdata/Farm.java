package com.example.kadir.agricultureprojectsupportside.datatypes.farmdata;


import com.example.kadir.agricultureprojectsupportside.datatypes.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;

public class Farm implements Serializable {
    public String farm_id;
    public int size;
    //measured in acres
    public float real_size;
    public ArrayList<Vector2> outline_points;
    public ArrayList<Module> modules;

    public Farm(){
        outline_points = new ArrayList<>();
        modules = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nfarm : " + farm_id + "\n {");
        sb.append(" outline_points {");
        for(Vector2 v : outline_points){
            sb.append("     " + v.toString() + "\n");
        }
        sb.append(" }\n");

        sb.append(" modules {");
        for(Module m : modules){
            sb.append("     " + m.toString() + "\n");
        }
        sb.append(" }\n");
        sb.append("}");

        return sb.toString();
    }
}
