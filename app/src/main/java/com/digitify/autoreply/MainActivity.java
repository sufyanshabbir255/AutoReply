package com.digitify.autoreply;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.digitify.autoreply.Adapters.ViewPagerAdapter;
import com.digitify.autoreply.Models.ORM;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_READ_SMS_PERMISSION = 3004;
    private final static int REQUEST_READ_PHONE_STATE = 3005;
    private final static String REG_CODE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String REG_CODE_REJECT_CALL = "android.intent.action.PHONE_STATE";
    final static int RQS_1 = 1;
    RequestPermissionAction onPermissionCallBack;

    long sharedPreferencesLongToken, token = 0;
    String incomingNumber, TokenNumber;
    private TabLayout tabLayout;
    SharedPreferences sharedPreferences;

    String number;
    private ViewPager viewPager;
    ArrayList<ORM> arrayList;
    private int[] tabIcons = {
            R.drawable.settings,
            R.drawable.history
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getReadSMSPermission(onPermissionCallBack);

    }

    private void initViews() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

    }

    private void initObj() {
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        arrayList = new ArrayList<>();
        setupTabIcons();
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.Key), Context.MODE_PRIVATE);


    }




    private void initListeners() {
        // toggle.setOnClickListener(mGlobal_OnClickListener);
    }

    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.toggleButton: {

                    break;
                }
            }
        }

    };

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SettingFragment(), "Configuration");
        viewPagerAdapter.addFragment(new HistoryFragment(), "History");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    public void registerSMSBroadcastReceiver() {
        IntentFilter smsFilter = new IntentFilter(REG_CODE_SMS_ACTION);
        smsFilter.setPriority(Integer.MAX_VALUE);
        IntentFilter callFilter = new IntentFilter(REG_CODE_REJECT_CALL);
        callFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, smsFilter);
        registerReceiver(smsSender, smsFilter);
        registerReceiver(callRejected, callFilter);
        //Toast.makeText(getActivity(), "check open", Toast.LENGTH_SHORT).show();
    }

    public void unRegisterSmsBroadCastReceiver() {
        try {
            unregisterReceiver(smsSender);
            unregisterReceiver(smsReceiver);
            unregisterReceiver(callRejected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    // A PDU is a "protocol autoReplyList unit". This is the industrial standard for SMS message
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        number = sms.getDisplayOriginatingAddress();

                        sharedPreferencesLongToken = sharedPreferences.getLong(getResources().getString(R.string.tokenKey), token);
                        SharedPreferences.Editor editor = sharedPreferences.edit();


                        List<ORM> list = ORM.findWithQuery(ORM.class,"select * from ORM where number=?",number);
                        if(list.size()==0)
                        {
                            sharedPreferencesLongToken = sharedPreferencesLongToken + 1;
                            editor.putLong(getResources().getString(R.string.tokenKey), sharedPreferencesLongToken);
                            editor.apply();
                            TokenNumber = String.valueOf(sharedPreferencesLongToken);
                            ORM sugarORM = new ORM(number, TokenNumber);
                            sugarORM.save();
                            Intent localIntent = new Intent("UpdateList");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                        }


                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    private BroadcastReceiver smsSender = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                String newMessage = sharedPreferences.getString(getResources().getString(R.string.message), "");
                SmsManager smsManager = SmsManager.getDefault();
                if (SettingFragment.checkOffDay.isChecked()) {
                    smsManager.sendTextMessage(number, null, newMessage, null, null);
                } else {

                    if (null!=TokenNumber) {
                        smsManager.sendTextMessage(number, null, newMessage + "\nToken Number is: " + TokenNumber, null, null);
                    }else{
                        List<ORM> list = ORM.findWithQuery(ORM.class,"select * from ORM where number=?",number);
                        smsManager.sendTextMessage(number, null, newMessage + "\nToken Number: " + list.get(0).getToken(), null, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private BroadcastReceiver callRejected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            if (myBundle != null) {
                try {
                    if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
                            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
                            methodGetITelephony.setAccessible(true);
                            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
                            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
                            methodEndCall.invoke(telephonyInterface);

                        }

                    }
                } catch (Exception ex) { // Many things can go wrong with reflection calls
                    ex.printStackTrace();
                }
            }
        }
    };

    private boolean checkReadSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                initObj();
                initListeners();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void getReadSMSPermission(RequestPermissionAction onPermissionCallBack) {
        this.onPermissionCallBack = onPermissionCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkReadSMSPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_SMS_PERMISSION);
                return;
            }
        }
        if (onPermissionCallBack != null)
            onPermissionCallBack.permissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            if (REQUEST_READ_SMS_PERMISSION == requestCode || REQUEST_READ_PHONE_STATE == requestCode) {
                initObj();
                initListeners();
            }
            if (onPermissionCallBack != null)
                onPermissionCallBack.permissionGranted();

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (REQUEST_READ_SMS_PERMISSION == requestCode || REQUEST_READ_PHONE_STATE == requestCode) {
                this.finish();
            }
            if (onPermissionCallBack != null)
                onPermissionCallBack.permissionDenied();
        }
    }

    public interface RequestPermissionAction {
        void permissionDenied();

        void permissionGranted();
    }

}