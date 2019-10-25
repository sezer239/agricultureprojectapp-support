package com.example.kadir.agricultureprojectsupportside.datatypes.math;

import com.example.kadir.agricultureprojectsupportside.vendors.snatik.polygon.Point;

import java.util.ArrayList;

public class MathUtils {
    public static float map(long x, long in_min, long in_max, long out_min, long out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
