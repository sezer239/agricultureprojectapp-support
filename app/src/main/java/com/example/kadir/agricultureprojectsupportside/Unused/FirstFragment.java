package com.example.kadir.agricultureprojectsupportside.Unused;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kadir.agricultureprojectsupportside.LoginPageFragment;
import com.example.kadir.agricultureprojectsupportside.R;
import com.example.kadir.agricultureprojectsupportside.ShortCut.OnSwipeTouchListener;

import butterknife.ButterKnife;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link FirstFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link FirstFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class FirstFragment extends Fragment {
 //   private OnFragmentInteractionListener mListener;
 //   FragmentActivity listener;
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, ph.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    @OnClick(R.id.goLoginPageButton) void goLoginPageActivity(){
//
//        Intent loginPageAct = new Intent(getActivity(),LoginPageActivity.class);
//        startActivity(loginPageAct);
//        getActivity().finish();
//    }
//
//    @OnClick(R.id.registerButton) void goRegisterPage(){
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
////        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
//    }

    private OnFragmentInteractionListener mListener;

    public FirstFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create soil_temperature new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment FirstFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static FirstFragment newInstance(String param1, String param2) {
//        FirstFragment fragment = new FirstFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        ButterKnife.bind(this, v);

        v.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
//                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
//                Toast.makeText(getActivity(), "bbb", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), RegisterPageActivity.class);
//                startActivity(intent);
//                //onDetach();
//                getActivity().finish();
//                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "aaa", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new LoginPageFragment());
                ft.addToBackStack(null);
                ft.setTransition(50000);
                ft.commit();
                //((RegisterPageActivity)getActivity()).loadLoginPageFragment();
                Toast.makeText(getActivity(), "onSwipeLeft", Toast.LENGTH_SHORT).show();
                //onDetach();
                //getActivity().finish();
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();}
            public void onSwipeBottom() {
//                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });

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
}
