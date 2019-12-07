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
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.TaskComplete;
import mcc.group14.apiclientapp.data.TaskDetails;
import mcc.group14.apiclientapp.data.TaskResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskList>{

    Context context;
    List<TaskDetails> list = new ArrayList<>();
    private static TaskDetails tasks;
    private static APIInterfaceJava apiInterfaceJava;
    private String requester_email;

    public TaskListAdapter(Context context, List<TaskDetails> list , String requester_email) {
        this.context = context;
        this.list =   list;
        this.requester_email = requester_email;
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
        apiInterfaceJava =  ProjectAPIJava.getClient().create(APIInterfaceJava.class);
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




                                    Call<ResponseBody> call = apiInterfaceJava.completeTask(new TaskComplete(tasks1.getId(), tasks1.getProjectId(), requester_email));
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if(response.code() == 200){
                                                holder.checkBox.setEnabled(false);
                                                holder.task_name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                                MDToast mdToast = MDToast.makeText(context, "Successfully marked the task as complete!", 3, MDToast.TYPE_SUCCESS);
                                                mdToast.show();
                                            }else {
                                                holder.checkBox.setChecked(false);
                                                MDToast mdToast = MDToast.makeText(context, "Oops! Some unexpected error occurred! Please try again later.", 3, MDToast.TYPE_ERROR);
                                                mdToast.show();

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            holder.checkBox.setChecked(false);
                                            MDToast mdToast = MDToast.makeText(context, "Oops! Some unexpected error occurred! Please try again later.", 3, MDToast.TYPE_ERROR);
                                            mdToast.show();

                                        }
                                    });
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

}