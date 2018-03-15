package com.digitify.autoreply.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.digitify.autoreply.Models.ORM;
import com.digitify.autoreply.R;

public class Receiver extends BroadcastReceiver {
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        ORM.deleteAll(ORM.class);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.Key), Context.MODE_PRIVATE);
        sharedPreferences.getLong(context.getString(R.string.tokenKey), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getString(R.string.tokenKey), 0);
        editor.apply();
        Intent localIntent = new Intent("UpdateList");
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

}
