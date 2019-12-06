package mcc.group14.apiclientapp.views.projects.dashboard;

import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;


import android.content.Intent;

import android.util.Log;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.ProjectDetail;

import mcc.group14.apiclientapp.data.ProjectsResponse;
import mcc.group14.apiclientapp.views.users.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import mcc.group14.apiclientapp.views.projects.tasks.TaskDashboard;

public class ProjectDashboard extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ProjectsResponse data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<ProjectCard> passToAdapter;
    private String userEmail,userAuth;

    //---------------------------------------




    //==================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_dashboard);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //======================Read Data================================

        Intent intent = getIntent();

        // get the info from login/sign-up
        userEmail = intent.getStringExtra("USER_EMAIL");
        userAuth = intent.getStringExtra("USER_AUTH");



        APIInterfaceJava apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        Call<ProjectsResponse> call = apiInterface.doGetListProjects(userEmail);
        call.enqueue(new Callback<ProjectsResponse>() {
            @Override
            public void onResponse(Call<ProjectsResponse> call, Response<ProjectsResponse> response) {


                Log.d("TAG",response.code()+"");


                data = response.body();
                passToAdapter = new ArrayList<>();
                for(ProjectsResponse.Payload d:data.payload ){
                    ProjectCard pCard = new ProjectCard();
                    pCard.projectName = d.name;
                    pCard.lastModified = d.last_modified;
                    pCard.projectType = d.project_type;
                    pCard.badge = d.badge;
                    passToAdapter.add(pCard);
                }

                //Log.d("Some",data.toString());

                adapter = new CustomAdapter(passToAdapter);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<ProjectsResponse> call, Throwable t) {
                call.cancel();
            }
        });


        //================================================================




    }


    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            //removeItem(v);

            Intent taskActivity = new Intent(context, TaskDashboard.class);
            context.startActivity(taskActivity);

        }


    }





}