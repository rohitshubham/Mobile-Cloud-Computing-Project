package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.hootsuite.nachos.NachoTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.Task;
import mcc.group14.apiclientapp.data.TaskCreateResponse;
import mcc.group14.apiclientapp.data.TaskResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskCreate extends AppCompatActivity {
    NachoTextView nachoTextView;
    private String project_id;
    private String team_member;
    TaskCreateResponse data;
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent  = getIntent();

        project_id = intent.getStringExtra("PROJECT_ID");
        team_member = intent.getStringExtra("TEAM_MEMBER");

        APIInterfaceJava apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_create);
        final DatePicker task_deadline = (DatePicker)findViewById(R.id.datePicker1);
        String[] suggestions = team_member.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        nachoTextView = (NachoTextView) findViewById(R.id.nacho_text_view);
        nachoTextView.setAdapter(adapter);

        final EditText task_name = (EditText) findViewById(R.id.editText2);
        final EditText task_desc = (EditText) findViewById(R.id.editText3);

        Button btn =  (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> task_members = nachoTextView.getChipValues();
                Date task_deadline_value = new Date(task_deadline.getYear() -1900, task_deadline.getMonth(), task_deadline.getDayOfMonth());
                String task_name_value = task_name.getText().toString();
                String task_desc_value = task_desc.getText().toString();
                if(task_name_value.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Task Name is Compulsory", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(task_desc_value.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Task Description is Compulsory", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date d = new Date();

                if(task_deadline_value.compareTo(d) <= 0){
                    Toast.makeText(getApplicationContext(), "Deadline can't be older than current date", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task task = new Task();
                task.deadline = task_deadline_value;
                task.name = task_name_value;
                task.project_id = project_id;
                task.description = task_desc_value;
                if(task_members.size() > 0){
                    task.status = "On-going";
                }else{
                    task.status = "Pending";
                }

                //Call HTTP method here
                Call<TaskCreateResponse> call = apiInterface.createTask(task);
                call.enqueue(new Callback<TaskCreateResponse>() {
                    @Override
                    public void onResponse(Call<TaskCreateResponse> call, Response<TaskCreateResponse> response) {

                        //start the intent
                        data = response.body();
                        Log.d("TAG",response.code()+"");
                        Log.d("TAG",data+"");

                    }

                    @Override
                    public void onFailure(Call<TaskCreateResponse> call, Throwable t) {
                        call.cancel();
                    }
                });

            }
        });
    }
}
