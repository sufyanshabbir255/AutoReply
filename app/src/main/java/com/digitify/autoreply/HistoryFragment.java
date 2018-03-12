package com.digitify.autoreply;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.digitify.autoreply.Adapters.HistoryAdapter;
import com.digitify.autoreply.Models.HistoryModel;
import com.digitify.autoreply.Models.ORM;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    Button clearList;
    HistoryAdapter adapter;
    String number;
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
        return view;
    }

    private void initViews(View view) {
        clearList = view.findViewById(R.id.clear_List);
        recyclerView = view.findViewById(R.id.history_recycler);
    }

    private void initObj() {
        setupAdapter();
    }

    private void initListeners() {
        clearList.setOnClickListener(mGlobal_OnClickListener);
    }

    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.clear_List: {
                    ORM.deleteAll(ORM.class);
                    setupAdapter();
                    break;
                }
            }
        }

    };

    private  void setupAdapter(){
        adapter = new HistoryAdapter(getContext(),getHistoryList());
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

    }
    ArrayList<HistoryModel> getHistoryList() {
        ArrayList<HistoryModel> autoReplyList = new ArrayList<HistoryModel>();
        List<ORM> locatons = ORM.listAll(ORM.class);
        for (ORM obj1 : locatons) {
            HistoryModel obj3 = new HistoryModel();
            obj3.number = obj1.number;
            autoReplyList.add(obj3);
        }
        return autoReplyList;
    }
}
