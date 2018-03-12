package com.digitify.autoreply;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;


public class SettingFragment extends Fragment {
    private CheckBox checkOffDay;
    ToggleButton toggle;
    TextView newText;
    Button editButton;
    AlertDialog.Builder dialogBuilder;
    AlertDialog builder;
    View dialogView;
    String newMessage;
    SharedPreferences settings,sharedPreferences;
    LayoutInflater inflater;
    String autoReplyText;
    public String DEFAULT="";
    //public String DEFAULT=getResources().getString(R.string.Closed_Text);
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
        checkClicked();
        return v;
    }

    private void checkClicked() {
        checkOffDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Sufyan", Toast.LENGTH_SHORT).show();
            }
        });
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
                ((MainActivity)getActivity()).registerSMSBroadcastReceiver();


            } else {
               // Toast.makeText(getActivity(), "check closed", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).unRegisterSmsBroadCastReceiver();

            }
        }
    });
}



    public void showAutoReplyWindow()
    {
        inflater = getActivity().getLayoutInflater();
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
                    settings = getActivity().getSharedPreferences(getResources().getString(R.string.Key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(getResources().getString(R.string.message), autoReplyText);
                    editor.apply();
                    setAutoReplyMessage();
                }
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.Cancel_Button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                //pass
            }
        });
        builder = dialogBuilder.create();
        builder.setCancelable(false);
        builder.show();
    }
    public void setAutoReplyMessage()
    {
        sharedPreferences=getActivity().getSharedPreferences(getResources().getString(R.string.Key),Context.MODE_PRIVATE);
        newMessage=sharedPreferences.getString(getResources().getString(R.string.message),DEFAULT);
        newText.setText(newMessage);

    }
}
