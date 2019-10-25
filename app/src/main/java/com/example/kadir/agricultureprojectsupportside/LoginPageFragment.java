package com.example.kadir.agricultureprojectsupportside;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.kadir.agricultureprojectsupportside.Interfaces.OnLoginUserCallback;
import com.example.kadir.agricultureprojectsupportside.ShortCut.ShortCut;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, ph.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseHelper firebaseHelper;
    private EditText emailLogin;
    private EditText passwordLogin;
//    DatabaseReference toprakNem = mDatabase.getReference("toprakNem");
//    DatabaseReference toprakIsı = mDatabase.getReference("toprakIsı");
//    DatabaseReference havaNem = mDatabase.getReference("havaNem");
//    DatabaseReference havaIsı = mDatabase.getReference("havaIsı");
//    DatabaseReference ph = mDatabase.getReference("ph");

    private String mEmail;
    private String mPassword;

//    @BindView(R.id.loginBtn)
//    Button login_button;

    //login butonuna basıldığında burası devreye girer..
    //   @OnClick(R.id.loginBtn) void loginButonClicked(){


//        mPassword = password.getText().toString();
//        eMail = email.getText().toString();
//        DatabaseReference mailRef = mDatabase.getReference("email");
//        DatabaseReference passwordRef = mDatabase.getReference("password");
//        mailRef.setValue(eMail);
//        passwordRef.setValue(mPassword);


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressBar progressBar;

    public LoginPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create soil_temperature new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginPageFragment newInstance(String param1, String param2) {
        LoginPageFragment fragment = new LoginPageFragment();
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
        View v = inflater.inflate(R.layout.fragment_login_page, container, false);

        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        firebaseHelper = new FirebaseHelper();
        mAuth = FirebaseAuth.getInstance();

        emailLogin = (EditText) v.findViewById(R.id.emailLoginPageFragment);
        passwordLogin = (EditText) v.findViewById(R.id.passwordLoginPageFragment);
        final Button buton = (Button) v.findViewById(R.id.loginBtn);

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin.setEnabled(false);
                passwordLogin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(emailLogin.getText()) || TextUtils.isEmpty(passwordLogin.getText())){
                    ShortCut.displayMessageToast(getActivity(), "Fill all fields!");
                    progressBar.setVisibility(View.INVISIBLE);
                    emailLogin.setEnabled(true);
                    passwordLogin.setEnabled(true);
                    return;
                }
                buton.setEnabled(false);
                mEmail = emailLogin.getText().toString();
                mPassword = passwordLogin.getText().toString();
                firebaseHelper.loginUser(getActivity(), mEmail, mPassword, new OnLoginUserCallback() {
                    @Override
                    public void onCorrectUserCallback() {}

                    @Override
                    public void onWrongUserCallback() {
                        buton.setEnabled(true);
                        emailLogin.setEnabled(true);
                        passwordLogin.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
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
