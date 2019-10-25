package com.example.kadir.agricultureprojectsupportside.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kadir.agricultureprojectsupportside.EditUserActivity;
import com.example.kadir.agricultureprojectsupportside.R;
import com.example.kadir.agricultureprojectsupportside.SezerMainActivity;

import java.util.ArrayList;

public class PopupRecyclerViewAdapter extends RecyclerView.Adapter<PopupRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> farmNames;
    private EditUserActivity.OnClickFarmName callback;

    LayoutInflater layoutInflater;

    public PopupRecyclerViewAdapter(Context context, ArrayList<String> farmNames, EditUserActivity.OnClickFarmName callback){
        this.context = context;
        this.farmNames = farmNames;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PopupRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.v("Popup", "crete view holder");
        layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.popup_recyclerview_row, viewGroup, false);
        PopupRecyclerViewAdapter.ViewHolder view_holder = new PopupRecyclerViewAdapter.ViewHolder(v);

        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PopupRecyclerViewAdapter.ViewHolder viewHolder, final int i) {

        Log.v("Popup", "bind calişio");

        viewHolder.mLinearLayout.setTag(viewHolder);
        viewHolder.mTextView.setText(farmNames.get(i)+"");
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mTextView.setEnabled(false);
                callback.onClickFarmName(farmNames.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return farmNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLinearLayout;
        private TextView mTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.popupRecyclerviewLinearRows);
            mTextView = itemView.findViewById(R.id.adding_farm_name_text);
        }
    }
}
