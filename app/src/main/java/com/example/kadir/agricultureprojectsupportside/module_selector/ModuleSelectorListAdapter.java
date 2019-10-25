package com.example.kadir.agricultureprojectsupportside.module_selector;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kadir.agricultureprojectsupportside.R;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Module;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.ModuleData;

import java.util.ArrayList;


public class ModuleSelectorListAdapter extends RecyclerView.Adapter<ModuleSelectorListAdapter.ModuleViewHolder> {
    private ArrayList<ModuleData> dataset;
    private ModuleSelectorFragment.OnSelectedItemCallback callback;
    private FragmentManager fm;

    // Provide soil_temperature suitable constructor (depends on the kind of dataset)
    public ModuleSelectorListAdapter(ArrayList<ModuleData> dataset, ModuleSelectorFragment.OnSelectedItemCallback cb , FragmentManager fm) {
        this.dataset = dataset;
        this.callback = cb;
        this.fm = fm;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ModuleViewHolder onCreateViewHolder(ViewGroup parent,
                                               final int viewType) {
        // create soil_temperature new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_text_view, parent, false);
        ModuleViewHolder vh = new ModuleViewHolder(v);
        return vh;
    }

    // Replace the contents of soil_temperature view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ModuleViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.on_selected_item_callback(dataset.get(position).module_id);
                fm.popBackStack();
            }
        });

        ModuleData m = dataset.get(position);
        StringBuilder sb = new StringBuilder();
        sb.append("HS ");
        sb.append((m.st_working ? m.soil_temperature :  "Çalışmıyor"));
        sb.append("\n");

        sb.append("HN ");
        sb.append((m.sh_working ? m.soil_humidity :  "Çalışmıyor"));
        sb.append("\n");

        sb.append("TS ");
        sb.append((m.at_working ? m.air_temperature :  "Çalışmıyor"));
        sb.append("\n");

        sb.append("TN ");
        sb.append((m.ah_working ? m.air_humidity :  "Çalışmıyor"));
        sb.append("\n");

        sb.append("PH ");
        sb.append((m.ph_working ? m.ph :  "Çalışmıyor"));
        sb.append("\n");

        holder.mTextView.setText("Modul no: " + dataset.get(position).module_id + sb.toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // Provide soil_temperature reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for soil_temperature data item in soil_temperature view holder
    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
//        public TextView text_detay;

        public ModuleViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.module_text);

        }
    }
}