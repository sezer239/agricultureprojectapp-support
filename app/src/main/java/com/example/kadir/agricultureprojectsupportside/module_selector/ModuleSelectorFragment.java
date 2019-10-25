package com.example.kadir.agricultureprojectsupportside.module_selector;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kadir.agricultureprojectsupportside.FirebaseHelper;
import com.example.kadir.agricultureprojectsupportside.Interfaces.OnGetFarmSensorData;
import com.example.kadir.agricultureprojectsupportside.R;
import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Module;
import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.ModuleData;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Random;

public class ModuleSelectorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, ph.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnSelectedItemCallback on_item_selected_callback;

    private RecyclerView recycler_view;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layout_manager;

    public ModuleSelectorFragment() {

    }

    public static ModuleSelectorFragment newInstance(String param1, String param2) {
        ModuleSelectorFragment fragment = new ModuleSelectorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_module_selector, container, false);

        recycler_view = v.findViewById(R.id.modlue_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler_view.setHasFixedSize(true);

        // use soil_temperature linear layout manager
        layout_manager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layout_manager);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public OnSelectedItemCallback getOn_item_selected_callback() {
        return on_item_selected_callback;
    }

    public void setOn_item_selected_callback(OnSelectedItemCallback on_item_selected_callback) {
        this.on_item_selected_callback = on_item_selected_callback;
        //  adapter.notifyDataSetChanged();
    }

    public interface OnSelectedItemCallback {
        void on_selected_item_callback(String moduleData);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <soil_temperature href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</soil_temperature> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void load_module_data(final ArrayList<String> used_module, final String farm_id) {



        FirebaseHelper.getUserFarmSensorData(FirebaseHelper.getmEditingUserId(), farm_id, new OnGetFarmSensorData() {
            @Override
            public void onFarmSensorData(ArrayList<ModuleData> moduleData) {
                ArrayList<ModuleData> refactored_module_data = new ArrayList<>();

                for (ModuleData m : moduleData) {
                    if (!used_module.contains(m.module_id)) {
                        refactored_module_data.add(m);
                    }
                }

                if (refactored_module_data.size() == 0) {
                    ShortCut.displayMessageToast(getContext() ,"Uygun kullanılmayan modul yok" );
                    getFragmentManager().popBackStack();
                } else {
                    adapter = new ModuleSelectorListAdapter(refactored_module_data, on_item_selected_callback, getFragmentManager());
                    recycler_view.setAdapter(adapter);
                }
            }
        });
    }
}
