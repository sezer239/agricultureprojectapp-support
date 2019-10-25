package com.example.kadir.agricultureprojectsupportside.datatypes.math;

import com.example.kadir.agricultureprojectsupportside.vendors.snatik.polygon.Point;

import java.io.Serializable;

public class Vector2 implements Serializable {

    public static final Vector2 up = new Vector2(0,-1);
    public static final Vector2 down = new Vector2(0,1);
    public static final Vector2 left = new Vector2(-1,0);
    public static final Vector2 right = new Vector2(1,0);

    public static double distance(Vector2 a, Vector2 b) {
        float v0 = b.x - a.x;
        float v1 = b.y - a.y;
        return Math.sqrt(v0*v0 + v1*v1);
    }

    public float x , y;

    public Vector2(){
        this.x = 0;
        this.y = 0;
    }

    public Vector2(Vector2 v){
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2(float x , float y){
        this.x = x;
        this.y = y;
    }

    //TODO: SEZER

    public Vector2 div(float c){
        return new Vector2(x / c , y /c);
    }

    public Vector2 add(Vector2 v){
        return new Vector2(this.x + v.x , this.y + v.y);
    }

    public Vector2 add(float x , float y){
        return new Vector2(this.x + x , this.y + y);
    }

    public Vector2 sub(Vector2 v){
        return new Vector2(this.x - v.x , this.y - v.y);
    }

    public Vector2 sub(float x , float y){
        return new Vector2(this.x - x , this.y - y);
    }

    public Vector2 mult(float c){
        return new Vector2(this.x * c, this.y * c);
    }

    public float mag(){
        return (float)Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        // sets length to 1
        //
        double length = Math.sqrt(x*x + y*y);

        if (length != 0.0) {
            float s = 1.0f / (float)length;
            x = x*s;
            y = y*s;
        }
    }

    public Point toPoint(){
        return new Point(x , y);
    }

    public Vector2 normalized() {
        Vector2 v = new Vector2(x , y);
        double length = Math.sqrt(v.x*v.x + v.y*v.y);

        if (length != 0.0) {
            float s = 1.0f / (float)length;
            v.x = v.x*s;
            v.y = v.y*s;
        }
        return v;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Vector2){
            Vector2 v = (Vector2)obj;
            return v.x == this.x && v.y == this.y;
        }
        return false;
    }

    public boolean equals(float x , float y){
        return x == this.x && y == this.y;
    }

    public boolean equals(int x , int y){
        return x == (int)this.x && y == (int)this.y;
    }

    @Override
    public String toString() {
        return "<" + x + " , " + y + ">";
    }
}
