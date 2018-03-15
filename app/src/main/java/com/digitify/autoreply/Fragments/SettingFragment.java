package com.digitify.autoreply.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.digitify.autoreply.Activities.MainActivity;
import com.digitify.autoreply.R;


public class SettingFragment extends Fragment {
    public static CheckBox checkOffDay;
    ToggleButton toggle;
    TextView newText;
    Button editButton;
    AlertDialog.Builder dialogBuilder;
    AlertDialog builder;
    View dialogView;
    String newMessage;
    SharedPreferences sharedPreferences;
    LayoutInflater inflater;
    String autoReplyText;
    public String DEFAULT="";
    LinearLayout linearLayout;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_setting, container, false);
        initViews(v);
        initObj();
        initListeners();
        setAutoReplyMessage();
        toogleClicked();
        return v;
    }

    private void initViews(View v)
    {
        toggle = v.findViewById(R.id.toggleButton);
        checkOffDay=v.findViewById(R.id.checkBox);
        editButton=v.findViewById(R.id.edit_Button);
        linearLayout=v.findViewById(R.id.lin);
        newText=v.findViewById(R.id.dumy_Text);
    }

    private void initListeners() {
        editButton.setOnClickListener(mGlobal_OnClickListener);
    }

    private void initObj()
    {
        inflater = getActivity().getLayoutInflater();
        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.Key), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(getResources().getString(R.string.button_state),false)){
            toggle.setChecked(true);
            ((MainActivity)getActivity()).registerSMSBroadcastReceiver();
        }
        else
        {
            toggle.setChecked(false);
        }
        dialogBuilder = new AlertDialog.Builder(getActivity());
    }

    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.edit_Button:
                    showAutoReplyWindow();
                    break;
            }
        }

    };

    public void toogleClicked()
    {
    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.button_state),true);
                editor.apply();
                ((MainActivity)getActivity()).registerSMSBroadcastReceiver();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.button_state),false);
                editor.apply();
                ((MainActivity)getActivity()).unRegisterSmsBroadCastReceiver();
            }
        }
    });
}

    public void showAutoReplyWindow()
    {
        dialogView = inflater.inflate(R.layout.popup_layout, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = dialogView.findViewById(R.id.edit);
        dialogBuilder.setTitle(getResources().getString(R.string.Popup_Title));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.Done_Editing), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                autoReplyText=edt.getText().toString();
                if (edt.length()==0)
                {
                  dialog.cancel();
                }
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.message), autoReplyText);
                    editor.apply();
                    setAutoReplyMessage();
                }
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.Cancel_Button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder = dialogBuilder.create();
        builder.setCancelable(false);
        builder.show();
    }

    public void setAutoReplyMessage()
    {
        newMessage=sharedPreferences.getString(getResources().getString(R.string.message),DEFAULT);
        newText.setText(newMessage);
    }

}
