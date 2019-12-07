package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.media.MediaCodecInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.data.TaskDetails;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskList>{

    Context context;
    List<TaskDetails> list = new ArrayList<>();
    private static TaskDetails tasks;

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

        tasks = list.get(position);
        holder.task_name.setText(tasks.getName());
        holder.checkBox.setChecked(tasks.isSelected());
        holder.checkBox.setTag(list.get(position));
        if (tasks.isSelected()){
            holder.task_name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setEnabled(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDetails tasks1 = (TaskDetails) holder.checkBox.getTag();
                if(holder.checkBox.isChecked()){

                   //tasks1.setSelected(holder.checkBox.isChecked());

                    new AlertDialog.Builder(context)
                            .setTitle("Complete Task")
                            .setMessage("Do you really want to set task " + tasks1.getName() +" to completed?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    MDToast mdToast = MDToast.makeText(context, "Attempting to save!", 3, MDToast.TYPE_INFO);
                                    mdToast.show();

                                    boolean result = CompleteTask();

                                    if(result){
                                        holder.checkBox.setEnabled(false);
                                        holder.task_name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                        mdToast = MDToast.makeText(context, "Successfully marked the task as complete!", 3, MDToast.TYPE_SUCCESS);
                                        mdToast.show();
                                    }else{
                                        holder.checkBox.setChecked(false);
                                        mdToast = MDToast.makeText(context, "Oops! Some unexpected error occurred! Please try again later.", 3, MDToast.TYPE_ERROR);
                                        mdToast.show();
                                    }
                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.checkBox.setChecked(false);
                                }
                            }).show();
                }else{
                    holder.checkBox.setChecked(true);
                    MDToast mdToast = MDToast.makeText(context, "This Task is already completed!", 3, MDToast.TYPE_WARNING);
                    mdToast.show();
                }
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

    public boolean CompleteTask(){
        return true;
    }
}