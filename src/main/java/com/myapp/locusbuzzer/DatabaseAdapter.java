package com.myapp.locusbuzzer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.MyViewHolder> {

    ArrayList<Task_Model> tasks;
    private OnTaskListener ontasklistener;
    private OnSwitchListener onswitchlitener;

    public DatabaseAdapter(ArrayList<Task_Model> tasks, OnTaskListener ontasklistener, OnSwitchListener onswitchlitener) {
        this.tasks = tasks;
        this.ontasklistener = ontasklistener;
        this.onswitchlitener = onswitchlitener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new MyViewHolder(view, ontasklistener, onswitchlitener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(tasks.get(position).getTitle());
        String Desc = tasks.get(position).getDesc();
        if(Desc.length()> 30)
            holder.desc.setText(Desc.substring(0,30)+".....");
        else
            holder.desc.setText(Desc);
        if(tasks.get(position).getIsActive().equalsIgnoreCase("yes"))
            holder.task_switch.setChecked(true);
        else
            holder.task_switch.setChecked(false);
    }

    @Override
    public int getItemCount() {
//        System.out.println("--------------"+tasks.size()+"-----------");
        return tasks.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, desc;
        Switch task_switch;
        OnTaskListener ontasklistener;
        public MyViewHolder(@NonNull View itemView, OnTaskListener ontasklistener, OnSwitchListener onswitchlitener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txt_tasktitle);
            desc = (TextView) itemView.findViewById(R.id.txt_taskdesc);
            task_switch = (Switch) itemView.findViewById(R.id.task_switch);

            task_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        onswitchlitener.onSwitchClick(tasks.get(getAbsoluteAdapterPosition()).getId(), "Yes");
//                      Toast.makeText(compoundButton.getContext(), getAdapterPosition()+"Switch On ", Toast.LENGTH_SHORT).show();
                    }else{
                        onswitchlitener.onSwitchClick(tasks.get(getAbsoluteAdapterPosition()).getId(), "No");
//                        Toast.makeText(compoundButton.getContext(), getAdapterPosition()+"Switch Off ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            this.ontasklistener = ontasklistener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ontasklistener.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskListener{
        void onTaskClick(int position);
    }

    public interface OnSwitchListener{
        void onSwitchClick(int position, String status);
    }
}
