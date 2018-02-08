package com.example.mrquentin.drawertest.Dialogs.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrquentin.drawertest.R;

/**
 * Created by MrQuentin on 06/02/2018.
 */

public class BasicTextDialogFragment extends DialogFragment {

    private TextView titileBox;
    private TextView contentBox;
    private ImageButton mImageButton;

    public BasicTextDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static BasicTextDialogFragment newInstance(String title, String content) {

        BasicTextDialogFragment frag = new BasicTextDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        //set the title of the Dialog
        titileBox = (TextView) view.findViewById(R.id.title_box);
        titileBox.setText(title);
        //set the content of the dialog
        contentBox = (TextView) view.findViewById(R.id.content_box);
        contentBox.setText(getArguments().getString("content", "This is the content!"));
        //create a clickable close button
        mImageButton = (ImageButton) view.findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }





}
