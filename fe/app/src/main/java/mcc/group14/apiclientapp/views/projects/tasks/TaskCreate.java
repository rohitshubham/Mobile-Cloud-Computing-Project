package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.hootsuite.nachos.NachoTextView;

import java.util.Date;
import java.util.List;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.data.Task;

public class TaskCreate extends AppCompatActivity {
    NachoTextView nachoTextView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_create);
        final DatePicker task_deadline = (DatePicker)findViewById(R.id.datePicker1);

        String[] suggestions = new String[]{"Tortilla Chips", "Melted Cheese", "Salsa", "Guacamole", "Mexico", "Jalapeno"};
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
                task.project_id = ""; //To be filled
                task.description = task_desc_value;

                //Call HTTP method here
            }
        });
    }
}
