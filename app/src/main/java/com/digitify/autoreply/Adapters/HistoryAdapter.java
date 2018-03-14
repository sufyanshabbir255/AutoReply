package com.digitify.autoreply.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitify.autoreply.Models.HistoryModel;
import com.digitify.autoreply.Models.ORM;
import com.digitify.autoreply.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<ORM> data;
    private Context context;
    private LayoutInflater inflater;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> historyList) {
        this.context = context;
        this.data = ORM.listAll(ORM.class);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.numberView.setText(data.get(position).getNumber());
        holder.tokenNumber.setText("Token #: "+data.get(position).getToken());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView numberView, tokenNumber;

        public MyViewHolder(View itemView) {
            super(itemView);
            numberView = itemView.findViewById(R.id.number);
            tokenNumber = itemView.findViewById(R.id.token_number);
        }
    }
}
