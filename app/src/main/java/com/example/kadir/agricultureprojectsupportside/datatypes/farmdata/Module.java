package com.example.kadir.agricultureprojectsupportside.datatypes.farmdata;


import com.example.kadir.agricultureprojectsupportside.datatypes.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;

public class Module implements Serializable {
//    public ModuleData module_data;
    public String module_id;
    public ArrayList<Vector2> points; // SO IT CAN BE DRAWABLE

    public Module(){
        points = new ArrayList<>();
       // module_data = new ModuleData();
    }

    //TODO: CLEAN THESE CODES
    public Vector2 get_top_left(){
        for(Vector2 v1 : points){
            for(Vector2 v2 : points){
                if(v1 == v2) continue;
                if(v1.x < v2.x && v1.y < v2.y){
                    return v1;
                }
            }
        }
        return null;
    }

    public Vector2 get_top_right(){
        for(Vector2 v1 : points){
            for(Vector2 v2 : points){
                if(v1 == v2) continue;
                if(v1.x > v2.x && v1.y < v2.y){
                    return v1;
                }
            }
        }
        return null;
    }

    public Vector2 get_bottom_left(){
        for(Vector2 v1 : points){
            for(Vector2 v2 : points){
                if(v1 == v2) continue;
                if(v1.x < v2.x && v1.y > v2.y){
                    return v1;
                }
            }
        }
        return null;
    }

    public Vector2 get_bottom_right(){
        for(Vector2 v1 : points){
            for(Vector2 v2 : points){
                if(v1 == v2) continue;
                if(v1.x > v2.x && v1.y > v2.y){
                    return v1;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        //TODO: MAKE THIS MORE READLABLELEJAJDOAIW
        if(obj instanceof Module){
            Module m = (Module)obj;
            boolean flag = false;
            for(Vector2 v1 : m.points){
                flag = false;
                for(Vector2 v2 : points){
                    if(v1.equals(v2)){
                        flag = true;
                    }
                }
                if(flag == false){
                    return  false;
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n points {");
        for(Vector2 v : points){
            sb.append("     " + v.toString() + "\n");
        }
        sb.append("}");

       // sb.append(module_data.toString());
        return sb.toString();
    }
}
