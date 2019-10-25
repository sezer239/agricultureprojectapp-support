package com.example.kadir.agricultureprojectsupportside;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;


import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Farm;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Module;
import com.example.kadir.agricultureprojectsupportside.datatypes.math.MathUtils;
import com.example.kadir.agricultureprojectsupportside.datatypes.math.Vector2;
import com.example.kadir.agricultureprojectsupportside.vendors.snatik.polygon.Line;
import com.example.kadir.agricultureprojectsupportside.vendors.snatik.polygon.Point;
import com.example.kadir.agricultureprojectsupportside.vendors.snatik.polygon.Polygon;

import java.util.ArrayList;

public class FarmEditView extends android.support.v7.widget.AppCompatImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final float MAX_SCALE_X = 8;
    private static final float MAX_SCALE_Y = 8;

    private static final float MIN_SCALE_X = 0.5f;
    private static final float MIN_SCALE_Y = 0.5f;

    private static final float MIN_PRODUCT_DRAW_DIST_X = 2;
    private static final float MIN_PRODUCT_DRAW_DIST_Y = 2;

    private static final String TAG = "FarmEditView";

    //INTERFACES
    public OnLongClickListener on_long_click_listener;
    public OnModuleClick on_module_click_listener;
    public OnPhaseChange on_phase_change_listener;
    public OnTouchDown on_touch_down;
    public OnTouchUp on_touch_up;

    //FLAGS
    private boolean allow_putting_data = false;
    private boolean can_remove_data = false;

    //BOOKEPING
    private EditPhases current_phase;
    private Control current_control;

    //GESTURE DETECTORS
    private GestureDetector gesture_detector_compat;
    private ScaleGestureDetector scale_gesture_detector;

    //DATA
    private Farm edited_farm;

    //FOR EDIT PHASE
    private ArrayList<Module> modules;
    private Vector2 unsanpped_mpos = new Vector2();

    //FOR OUTLINE DRAW PHASE
    private ArrayList<Vector2> outline_points;
    private ArrayList<Vector2> temp_points;
    private Vector2 start_offset = new Vector2(32, 32);  // HOW FAR IT WILL START FROM x and y : for this instance it will start from + 16 , offset from x and y
    private boolean is_snapped = false;
    private Vector2 snapped_mpos = new Vector2(0, 0);

    //SAME IN BOTH
    private Canvas canvas_ref; // might need in the futre
    private Vector2 canvas_bounds = new Vector2(0, 0);
    private Vector2 dxdy = new Vector2(0, 0);
    private Paint cell_paint = new Paint();
    private Paint paint = new Paint();
    private Paint text_paint = new Paint();

    //FOR COLLISION DETECTION
    private Polygon outline_shape;

    //CAMERA
    private Vector2 scale = new Vector2(1, 1);
    private Vector2 offset = new Vector2(0, 0);
    private Vector2 avg_mpos = new Vector2(0, 0);

    //PRECALUACTED DATA
    private ArrayList<ArrayList<Vector2>> cell;

    public FarmEditView(Context context) {
        super(context);
        init(context);
    }

    public FarmEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FarmEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void load_farm(Farm f, boolean is_display_only) {
        load_farm(f);
        if (is_display_only) change_phase(EditPhases.DISPLAY_ONLY_PHASE);
    }

    public void load_farm(Farm f) {
        if (f == null) {
            Log.v(TAG, "Loaded farm is nulll");
            return;
        }

        Log.v(TAG, f.farm_id + " is loaded");

        if (f.outline_points != null && f.outline_points.size() >= 3 && check_if_outline_is_closed(f.outline_points)) {
            edited_farm = f;
            modules = f.modules;
            outline_points = f.outline_points;
            change_phase(EditPhases.MODULE_PLACEMENT_PHASE);
        } else {
            change_phase(EditPhases.OUTLINE_DRAW_PHASE);
            edited_farm = f;
        }

        build_outline_shape();
        initilize_fixup_cell_data();
    }

    public void save_farm() {
        if (outline_points != null && check_if_outline_is_closed(outline_points)) {
            edited_farm.outline_points = outline_points;

            float real_size = 0;
            for (ArrayList<Vector2> v : cell) {
                if (v.size() == 4) {
                    real_size++;
                } else if (v.size() == 3) {
                    real_size += 0.5f;
                }
            }
            edited_farm.real_size = real_size;
            if (modules != null)
                edited_farm.modules = modules;
            Log.v(TAG, edited_farm.toString());
        } else {
            Toast.makeText(getContext(), "Finish Drawing First", Toast.LENGTH_LONG).show();
        }
    }

    public void change_farm_size(int size) {
        if (current_phase != EditPhases.OUTLINE_DRAW_PHASE) return;
        edited_farm.size = size;
        clean();
        load_farm(edited_farm);
    }

    private void init(Context context) {
        current_control = Control.EDIT;
        outline_points = new ArrayList<>();
        temp_points = new ArrayList<>();
        modules = new ArrayList<>();
        cell = new ArrayList<>();

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);

        cell_paint.setColor(Color.BLUE);
        cell_paint.setAlpha(120);
        cell_paint.setStyle(Paint.Style.FILL);

        text_paint.setColor(Color.BLACK);
        text_paint.setTextSize(10);
        text_paint.setTextAlign(Paint.Align.CENTER);
        text_paint.setFakeBoldText(true);

        scale_gesture_detector = new ScaleGestureDetector(getContext(), this);
        gesture_detector_compat = new GestureDetector(getContext(), this);
    }

    private void init_bounds(Canvas canvas) {
        this.canvas_ref = canvas;
        canvas_bounds.x = this.canvas_ref.getWidth();
        canvas_bounds.y = this.canvas_ref.getHeight();

        dxdy.x = (canvas_bounds.x + start_offset.x) / edited_farm.size;
        dxdy.y = (canvas_bounds.y + start_offset.y) / edited_farm.size;
    }

    private boolean check_if_outline_is_closed(ArrayList<Vector2> lines) {
        if (lines == null || lines.size() < 3) return false;
        return lines.get(0).equals(lines.get(lines.size() - 1));
    }


    private void finger_up_behaviour_outline_draw_phase(MotionEvent event) {
        //RANGES FROM 0 to matrix lenght
        if (current_control != Control.EDIT || outline_points.size() == 0) return;

        if (outline_points.size() == 1 && temp_points.size() <= 1) {
            outline_points.clear();
        } else if (!outline_points.get(outline_points.size() - 1).equals(snapped_mpos.x, snapped_mpos.y)) { // IF DIDNT DRAG ALL THE WAYY BUT RETURNED THE BEGING
            outline_points.addAll(temp_points);
            temp_points.clear();

            if (check_if_outline_is_closed(outline_points) && outline_points.size() >= 3) {
                Toast.makeText(getContext(), "State Changed", Toast.LENGTH_LONG).show();
                change_phase(EditPhases.MODULE_PLACEMENT_PHASE);
                build_outline_shape();
                initilize_fixup_cell_data();
            }
        }
        is_snapped = false;
    }

    private void build_outline_shape() {
        if (outline_points.size() < 3) return;
        Polygon.Builder builder = Polygon.Builder();

        for (Vector2 v : outline_points) {
            builder.addVertex(v.toPoint());
        }

        outline_shape = builder.build();
    }

    public void undo() {

        if (current_phase == EditPhases.OUTLINE_DRAW_PHASE) {
            if (outline_points.size() > 0) {
                outline_points.remove(outline_points.size() - 1);
                if (outline_points.size() == 1) outline_points.clear();
            }
        } else if (current_phase == EditPhases.MODULE_PLACEMENT_PHASE) {
            if (modules.size() > 0)
                modules.remove(modules.size() - 1);
        }
    }

    public void clean() {
        temp_points.clear();
        outline_points.clear();
        cell.clear();
        modules.clear();
        if (edited_farm.outline_points != null) {
            edited_farm.outline_points.clear();
        }

        if (edited_farm.modules != null) {
            edited_farm.modules.clear();
        }

        change_phase(EditPhases.OUTLINE_DRAW_PHASE);
        scale.x = 1;
        scale.y = 1;
        offset.x = 0;
        offset.y = 0;
    }

    private void finger_down_behaviour_outline_draw_phase(MotionEvent event) {
        if (current_control != Control.EDIT) return;
        Vector2 current_snapped_point = new Vector2(0, 0);
        current_snapped_point.x = snapped_mpos.x;
        current_snapped_point.y = snapped_mpos.y;

        if (outline_points.size() == 0) { // IF THIS IS THE FIRST TIME ADD THIS AS A BEGINING PONT
            //Log.v(TAG, "Add the first point " + current_snapped_point.toString());
            outline_points.add(new Vector2(current_snapped_point));
        }

        if (outline_points.get(outline_points.size() - 1).equals(current_snapped_point)) { // IF I PRESSED THE LAST EDITED PLACE THEN I ALLOW DRAWING
            //Log.v(TAG, "Allow Drawing is setted to true");
            is_snapped = true;
        }
    }

    private void finger_move_behaviour_outline_draw_phase(MotionEvent event) {
        if (current_control != Control.EDIT) {
            temp_points.clear();
            return;
        }

        if (is_snapped) {
            temp_points.clear();
            Vector2 last_point = outline_points.get(outline_points.size() - 1);
            temp_points.add(last_point);
            int diff_x = (int) last_point.x - (int) snapped_mpos.x; // how many steps in x
            int diff_y = (int) last_point.y - (int) snapped_mpos.y; // how many steps in y

            int x = (int) last_point.x;
            int y = (int) last_point.y;
            while (diff_x != 0 || diff_y != 0) {

                if (diff_x < 0) {
                    x++;
                    diff_x++;
                }
                if (diff_x > 0) {
                    x--;
                    diff_x--;
                }
                if (diff_y < 0) {
                    y++;
                    diff_y++;
                }
                if (diff_y > 0) {
                    y--;
                    diff_y--;
                }

                Log.v(TAG, "size " + temp_points.size());

                if (outline_points.get(0).equals(x, y)) {
                    temp_points.add(new Vector2(x, y));
                    break;
                }
                if (does_outline_points_contains(x, y)) {
                    break;
                }

                temp_points.add(new Vector2(x, y));
            }
        }
    }

    private boolean does_outline_points_contains(int x, int y) {
        for (Vector2 v : outline_points) {
            if (v.equals(x, y)) {
//                Log.v(TAG, "CONTAINS");
                return true;
            }
        }
        return false;
    }

    private void draw_line_from_points(Canvas canvas, ArrayList<Vector2> points) {

        Vector2 first_point = null;
        for (Vector2 v : points) {
            if (first_point == null) {
                first_point = v;
            } else {
                float x1 = first_point.x * dxdy.x + start_offset.x;
                float y1 = first_point.y * dxdy.y + start_offset.y;
                float x2 = v.x * dxdy.x + start_offset.x;
                float y2 = v.y * dxdy.y + start_offset.y;
                //Log.v(TAG, "DRAING AT <" + x1 + " , " + y1 + " , " + x2 + " , " + y2 + "> ");
                canvas.drawLine(x1, y1, x2, y2, paint);
                first_point = v;
            }
        }
    }

    private void draw_grids(Canvas canvas) {
        for (int y = 0; y < edited_farm.size ; y++) {
            for (int x = 0; x < edited_farm.size; x++) {
                float x0 = x * dxdy.x + start_offset.x;
                float y0 = y * dxdy.y + start_offset.y;
                if (outline_shape.contains(new Point(x + 0.2f, y + 0.1f))) {
                    canvas.drawLine(x0, y0, (x + 1) * dxdy.x + start_offset.x, y0, paint);
                }
                if (outline_shape.contains(new Point(x + 0.1f, y + 0.2f))) {
                    canvas.drawLine(x0, y0, x0, (y + 1) * dxdy.y + start_offset.y, paint);
                }

            }
        }
    }

    private void draw_cells(Canvas canvas, boolean show_module_ids) {
        Path path = new Path();

        for (Module module : modules) {
            path.reset();
            path.moveTo(module.points.get(0).x * dxdy.x + start_offset.x, module.points.get(0).y * dxdy.y + start_offset.y);
            Vector2 avg = new Vector2();
            for (Vector2 point : module.points) {
                path.lineTo(point.x * dxdy.x + start_offset.x, point.y * dxdy.y + start_offset.y);
                avg = avg.add(new Vector2(point.x * dxdy.x + start_offset.x, point.y * dxdy.y + start_offset.y));
            }
            avg = avg.div(module.points.size());

            canvas.drawPath(path, cell_paint);
            if (show_module_ids) {
                canvas.drawText(module.module_id, avg.x, avg.y, text_paint);
            }
        }
    }

    //Tries to cast a rectangular or triangular area wrt outline_shape by the given position
    //only valid if returned array size > 2
    //all of these calculations are in normalized space this means each dot is 1 unit away from each other
    private ArrayList<Vector2> cast_fill(float x, float y) {
        float mx = x;
        float my = y;

        ArrayList<Vector2> m = new ArrayList<>();

        Vector2 center = new Vector2((float) Math.floor(mx) + 0.5f, (float) Math.floor(my) + 0.5f);

        /*
         *       *********
         *       **  a  **
         *       **    * *
         *       * *  *  *
         *       *d **  b*
         *       *  **   *
         *       * *  *  *
         *       **  c  **
         *       *********
         * */

        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;

        if (outline_shape.contains(center.add(Vector2.up.mult(0.2f)).toPoint())) {
            a = true;
        }
        if (outline_shape.contains(center.add(Vector2.right.mult(0.2f)).toPoint())) {
            b = true;
        }
        if (outline_shape.contains(center.add(Vector2.down.mult(0.2f)).toPoint())) {
            c = true;
        }
        if (outline_shape.contains(center.add(Vector2.left.mult(0.2f)).toPoint())) {
            d = true;
        }


        if (a & b & c & d) {
            m.add(new Vector2(center.add(new Vector2(-0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, 0.5f))));
            m.add(new Vector2(center.add(new Vector2(-0.5f, 0.5f))));
//            Log.v(TAG, "ABCD");
        } else if (a & b) {
            m.add(new Vector2(center.add(new Vector2(-0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, 0.5f))));
//            Log.v(TAG, "AB");
        } else if (a & d) {
            m.add(new Vector2(center.add(new Vector2(-0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(-0.5f, 0.5f))));
//            Log.v(TAG, "AD");
        } else if (c & b) {
            m.add(new Vector2(center.add(new Vector2(0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, 0.5f))));
            m.add(new Vector2(center.add(new Vector2(-0.5f, 0.5f))));
//            Log.v(TAG, "CB");
        } else if (c & d) {
            m.add(new Vector2(center.add(new Vector2(-0.5f, -0.5f))));
            m.add(new Vector2(center.add(new Vector2(0.5f, 0.5f))));
            m.add(new Vector2(center.add(new Vector2(-0.5f, 0.5f))));
//            Log.v(TAG, "CD");
        }

        return m;
    }

    private void draw_dot_matrix(Canvas canvas, float radius, Paint paint) {
        for (int y = 0; y < edited_farm.size; y++) {
            for (int x = 0; x < edited_farm.size; x++) {
                float x0 = x * dxdy.x + start_offset.x;
                float y0 = y * dxdy.y + start_offset.y;
                if (outline_points.contains(new Vector2(x, y)))
                    canvas.drawCircle(x0, y0, radius * 1.8f, paint);
                else if (current_phase == EditPhases.OUTLINE_DRAW_PHASE)
                    canvas.drawCircle(x0, y0, radius, paint);

            }
        }
    }

    public void initilize_fixup_cell_data() {
        if(outline_points.size() == 0) return;
        for (int y = 0; y < edited_farm.size; y++) {
            for (int x = 0; x < edited_farm.size; x++) {
                ArrayList<Vector2> p;
                p = cast_fill(x + 0.5f, y + 0.5f);
                if (p != null && p.size() >= 3) {
                    cell.add(p);
                }
            }
        }
    }

    public void put_data_onto_last_selected_place(String module_id) {
        if (!allow_putting_data) return;

        Module m = new Module();
        m.points = cast_fill(unsanpped_mpos.x, unsanpped_mpos.y);

        if (m.points.size() <= 2) {
//            Log.v(TAG, "Module cannot be added");
            return;
        } else {
//            Log.v(TAG, module_id + " is added to " + m.toString());
            m.module_id = module_id;
            modules.add(m);
        }
    }

    public Module remove_data_last_selected_place() {
        Module removed_module = null;
        Module m = new Module();
        m.points = cast_fill(unsanpped_mpos.x, unsanpped_mpos.y);
        if (m.points.size() >= 2 && can_remove_data) {
            int index = -1;
            if ((index = modules.indexOf(m)) >= 0) {
                removed_module = modules.get(index);
                modules.remove(index);
                can_remove_data = false;
            }
        }

        return removed_module;
    }

    private void finger_down_behaviour_module_placement_phase(MotionEvent event) {

        Module m = new Module();
        m.points = cast_fill(unsanpped_mpos.x, unsanpped_mpos.y);

        if (m.points.size() <= 2) {
            allow_putting_data = false;
        } else {
            int index = modules.indexOf(m);
            if (index >= 0) {
//                Log.v(TAG, "INDEX IS " + index);
                allow_putting_data = false;
                can_remove_data = true;
            } else {
                allow_putting_data = true;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        avg_mpos = avg_mpos.mult(0);
        for (int i = 0; i < event.getPointerCount(); i++) {
            avg_mpos = avg_mpos.add(new Vector2(event.getX(i), event.getY(i)));
        }
        avg_mpos = avg_mpos.div(event.getPointerCount());

        if (current_phase == EditPhases.DISPLAY_ONLY_PHASE) return super.onTouchEvent(event);
        //RESET THE CONTOL PHASES
        scale_gesture_detector.onTouchEvent(event);
        gesture_detector_compat.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        unsanpped_mpos.x = (x - start_offset.x * scale.x - offset.x) / (dxdy.x * scale.x);
        unsanpped_mpos.y = (y - start_offset.y * scale.y - offset.y) / (dxdy.y * scale.y);

        snapped_mpos.x = (int) Math.round((x - start_offset.x * scale.x - (offset.x)) / (dxdy.x * scale.x));
        snapped_mpos.y = (int) Math.round((y - start_offset.y * scale.y - (offset.y)) / (dxdy.y * scale.y));


        if (outline_shape != null && outline_shape.contains(unsanpped_mpos.toPoint())) {
//            Log.v(TAG, "CONTAINS");
        } else {
//            Log.v(TAG, "NOT CONTAINS");
        }

        if (snapped_mpos.x >= 0 && snapped_mpos.x < edited_farm.size && snapped_mpos.y >= 0 && snapped_mpos.y < edited_farm.size) {

            if (current_phase == EditPhases.OUTLINE_DRAW_PHASE) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        current_control = Control.EDIT;
                        finger_up_behaviour_outline_draw_phase(event);
                        if(on_touch_up != null) on_touch_up.on_touch_up();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        finger_down_behaviour_outline_draw_phase(event);
                        if(on_touch_down != null) on_touch_down.on_touch_down();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        finger_move_behaviour_outline_draw_phase(event);
                        break;
                }
            } else if (current_phase == EditPhases.MODULE_PLACEMENT_PHASE) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        current_control = Control.EDIT;
                        if(on_touch_up != null) on_touch_up.on_touch_up();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        finger_down_behaviour_module_placement_phase(event);
                        if(on_touch_down != null) on_touch_down.on_touch_down();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
            }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas_ref == null) {
            init_bounds(canvas); // RUNS ONLY ONE IN THE BEGGING
        }

        canvas.save();

        canvas.translate(offset.x, offset.y);
        canvas.scale(scale.x, scale.y);

        if (current_phase == EditPhases.OUTLINE_DRAW_PHASE) {
            draw_dot_matrix(canvas, 8, paint);
            if (is_snapped)
                draw_line_from_points(canvas, temp_points);

        } else if (current_phase == EditPhases.MODULE_PLACEMENT_PHASE || current_phase == EditPhases.DISPLAY_ONLY_PHASE) {
            draw_grids(canvas);
            draw_cells(canvas, true);
        }

        draw_line_from_points(canvas, outline_points);

        canvas.restore();
        invalidate();
        super.onDraw(canvas);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Log.v(TAG , "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //  Log.v(TAG , "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Log.v(TAG , "onSingleTapUp");
        Module m = new Module();
        m.points = cast_fill(unsanpped_mpos.x, unsanpped_mpos.y);
        int index = -1;
        if (m.points.size() >= 2 && (index = modules.indexOf(m)) >= 0) {
            on_module_click_listener.on_module_click(modules.get(index).module_id);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Log.v(TAG , "onScroll " + e1.getPointerCount() + " , " + e2.getPointerCount());

        if ((e1.getPointerCount() == 1 && e2.getPointerCount() == 2 || e1.getPointerCount() == 2 && e2.getPointerCount() == 1)) {
            current_control = Control.DRAG;
            offset.x -= distanceX;
            offset.y -= distanceY;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        on_long_click_listener.onLongClick(this);
        //Log.v(TAG , "onLongPress");
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Log.v(TAG , "Fling " + e1.toString() + " , " + e2.toString() + " <" + velocityX + "," + velocityY + ">");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // Log.v(TAG , "onSingleTapConfiremd");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //TODO: ALLOW SCROLLING
        // Log.v(TAG , "onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scf = detector.getScaleFactor();
        boolean cannot_zoom = false;
        current_control = Control.ZOOM;

        scale.x *= scf;
        scale.y *= scf;

        if (scale.x > MAX_SCALE_X) {
            scale.x = MAX_SCALE_X;
            cannot_zoom = true;
        }
        if (scale.x < MIN_SCALE_X) {
            scale.x = MIN_SCALE_X;
            cannot_zoom = true;
        }

        if (scale.y > MAX_SCALE_Y) {
            scale.y = MAX_SCALE_Y;
            cannot_zoom = true;
        }
        if (scale.y < MIN_SCALE_Y) {
            scale.y = MIN_SCALE_Y;
            cannot_zoom = true;
        }
        //cannot NOT zoom
        if (!cannot_zoom) {
            float d = detector.getCurrentSpan() - detector.getPreviousSpan();
            Vector2 temp = avg_mpos.sub(offset).normalized();
            offset.x -= temp.x * d * scale.x;
            offset.y -= temp.y * d * scale.y;
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        //Log.v(TAG , "onScaleBegin");
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // Log.v(TAG , "onScaleEnd");
    }

    public Farm edited_farm() {
        return edited_farm;
    }

    public boolean allow_putting_data() {
        return allow_putting_data;
    }

    public ArrayList<String> getModule_data() {
        ArrayList<String> temp = new ArrayList<>();
        for (Module m : modules) {
            temp.add(m.module_id);
        }
        return temp;
    }

    private void change_phase(EditPhases nextPhase) {
        EditPhases prev = current_phase;
        current_phase = nextPhase;
        if (on_phase_change_listener == null) return;
        on_phase_change_listener.on_phase_change(prev, current_phase);
    }

    public EditPhases current_phase() {
        return current_phase;
    }

    public boolean can_remove_data() {
        return can_remove_data;
    }

    public enum EditPhases {OUTLINE_DRAW_PHASE, MODULE_PLACEMENT_PHASE, DISPLAY_ONLY_PHASE}

    private enum Control {EDIT, ZOOM, DRAG}

    public interface OnTouchDown{
        void on_touch_down();
    }

    public interface OnTouchUp{
        void on_touch_up();
    }

    public interface OnModuleClick {
        void on_module_click(String module_id);
    }

    public interface OnPhaseChange {
        void on_phase_change(EditPhases prev_phase, EditPhases current_phase);
    }
}