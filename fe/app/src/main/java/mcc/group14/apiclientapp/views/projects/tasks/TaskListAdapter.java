package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.data.TaskDetails;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskList>{

    Context context;
    List<TaskDetails> list = new ArrayList<>();

    public TaskListAdapter(Context context, List<TaskDetails> list ) {
        this.context = context;
        this.list =   list;
    }

    @Override
    public TaskList onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(context).inflate(R.layout.task_list, parent,false);

        return new TaskList(view);
    }

    @Override
    public void onBindViewHolder(final TaskList holder, final int position) {

        final TaskDetails fruits = list.get(position);
        holder.task_name.setText(fruits.getName());
        holder.checkBox.setChecked(fruits.isSelected());
        holder.checkBox.setTag(list.get(position));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                TaskDetails fruits1 = (TaskDetails) holder.checkBox.getTag();

                fruits1.setSelected(holder.checkBox.isChecked());

                list.get(position).setSelected(holder.checkBox.isChecked());

                for (int j=0; j<list.size();j++){

                    if (list.get(j).isSelected() == true){
                        data = data + "\n" + list.get(j).getName().toString() + "   " + list.get(j).getId().toString();
                    }
                }
                Toast.makeText(context, "Selected Fruits : \n " + data, Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setMessage("Do you really want to whatever?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(context, "Yaay", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TaskList extends RecyclerView.ViewHolder{

        TextView task_name;
        CheckBox checkBox;

        public TaskList(View itemView) {
            super(itemView);

            task_name = itemView.findViewById(R.id.task_name);
            checkBox = itemView.findViewById(R.id.checkBox_select);
        }
    }

    public List<TaskDetails> getTaskDetailsList(){
        return  list;
    }

}