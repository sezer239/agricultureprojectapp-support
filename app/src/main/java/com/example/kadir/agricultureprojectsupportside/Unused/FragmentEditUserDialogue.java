package com.example.kadir.agricultureprojectsupportside.Unused;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.kadir.agricultureprojectsupportside.R;

public class FragmentEditUserDialogue extends DialogFragment {


        private EditText mEditText;

        public FragmentEditUserDialogue() {
            // Empty constructor is required for DialogFragment
            // Make sure not to add arguments to the constructor
            // Use `newInstance` instead as shown below
        }

        public static FragmentEditUserDialogue newInstance(String title) {
            FragmentEditUserDialogue frag = new FragmentEditUserDialogue();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_edit_user, container);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Get field from view
            mEditText = (EditText) view.findViewById(R.id.txt_your_name);
            // Fetch arguments from bundle and set title
            String title = getArguments().getString("title", "Enter Name");
            getDialog().setTitle(title);
            // Show soft keyboard automatically and request focus to field
            mEditText.requestFocus();
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

