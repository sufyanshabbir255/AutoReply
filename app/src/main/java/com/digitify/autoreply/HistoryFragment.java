package com.digitify.autoreply;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.digitify.autoreply.Adapters.HistoryAdapter;
import com.digitify.autoreply.Models.HistoryModel;
import com.digitify.autoreply.Models.ORM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    HistoryAdapter adapter;
    String number;
    Calendar cal, current;
    final static int RQS_1 = 1;
    RecyclerView.LayoutManager layoutManager;


    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initViews(view);
        initObj();
        initListeners();
        setTimeForClearList();
        registerReceiver();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.history_recycler);
        cal = Calendar.getInstance();
        current = Calendar.getInstance();
    }

    private void initObj() {
        setupAdapter();
    }

    private void initListeners() {
    }

    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch (v.getId()) {

            }
        }

    };

    public void setTimeForClearList() {
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);

        if (cal.compareTo(current) <= 0) {

        } else {
            setAlarm(cal);
        }
    }

    private void setAlarm(Calendar targetCal) {
        Intent intent = new Intent(getActivity(), Receiver.class);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        setupAdapter();
    }
    public void registerReceiver()
    {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateListReceiver, new IntentFilter("UpdateList")
        );

    }

    public void setupAdapter() {
        adapter = new HistoryAdapter(getContext(), getHistoryList());
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    ArrayList<HistoryModel> getHistoryList() {
        ArrayList<HistoryModel> autoReplyList = new ArrayList<HistoryModel>();
        List<ORM> locatons = ORM.listAll(ORM.class);
        for (ORM obj1 : locatons) {
            HistoryModel obj3 = new HistoryModel();
            obj3.number = obj1.getNumber();
            autoReplyList.add(obj3);
        }
        return autoReplyList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(updateListReceiver);
    }

    private BroadcastReceiver updateListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupAdapter();
        }
    };
}
