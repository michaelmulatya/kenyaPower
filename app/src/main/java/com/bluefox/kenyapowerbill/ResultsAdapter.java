package com.bluefox.kenyapowerbill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private List<Data> dataList;
    private Context context;

    public ResultsAdapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Data data = dataList.get(position);
        holder.amount.setText(data.getAmount());
        holder.charge.setText(data.getCharge());
        //onclicl listener for each explanationn
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog for each explanation
                String charge = data.getCharge();

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView charge, amount;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            charge = itemView.findViewById(R.id.charge_tv);
            amount = itemView.findViewById(R.id.amount_tv);
            linearLayout = itemView.findViewById(R.id.card_linearlayout);

        }
    }
}
