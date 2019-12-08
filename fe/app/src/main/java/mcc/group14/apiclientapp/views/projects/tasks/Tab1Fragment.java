package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.ProjectsResponse;
import mcc.group14.apiclientapp.data.TaskDetails;
import mcc.group14.apiclientapp.data.TaskResponse;
import mcc.group14.apiclientapp.views.projects.dashboard.CustomAdapter;
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectCard;
import mcc.group14.apiclientapp.views.users.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab1Fragment extends Fragment {
    private Context mContext;
    private String project_id;
    private String team_members;
    private String requester_email;
    private String project_name;
    private static TaskResponse data;
    private static RecyclerView recyclerView;
    private static ArrayList<TaskDetails> passToAdapter;
    private static View view;
    private static FloatingActionButton fab;
    private static ProgressBar spinner;
    private static APIInterfaceJava apiInterface;
    TaskListAdapter tlAdapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    @Override
    public void onResume(){
        super.onResume();
        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar4);
        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_task_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            project_id = bundle.getString("project_id");
            team_members = bundle.getString("team_members");
            requester_email = bundle.getString("requester_email");
            project_name = bundle.getString("project_name");

            try{
                Call<TaskResponse> call = apiInterface.doGetListTasks(project_id, requester_email);

                call.enqueue(new Callback<TaskResponse>() {
                    @Override
                    public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {


                        Log.d("TAG",response.code()+"");
                        Log.d("Test",response.toString());

                        try{
                            data = response.body();
                            passToAdapter = new ArrayList<>();
                            for(TaskResponse.Payload d:data.payload ){
                                TaskDetails tCard = new TaskDetails(d.name, d.task_id, d.status.equalsIgnoreCase("Complete"), d.status, d.project_id);
                                passToAdapter.add(tCard);
                            }

                        }
                        catch (Exception e){
                            Log.d("Tag", e.getMessage());
                        }
                        spinner.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        tlAdapter = new TaskListAdapter(mContext, passToAdapter, requester_email);
//                    adapter = new CustomAdapter(passToAdapter,mContext);
                        recyclerView.setAdapter(tlAdapter);

                    }

                    @Override
                    public void onFailure(Call<TaskResponse> call, Throwable t) {
                        call.cancel();
                        spinner.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (Exception e){
                spinner.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
            }

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taskCreateActivity = new Intent(mContext, TaskCreate.class);
                taskCreateActivity.putExtra("PROJECT_ID", project_id);
                taskCreateActivity.putExtra("TEAM_MEMBER", team_members);
                taskCreateActivity.putExtra("REQUESTER_EMAIL", requester_email);
                taskCreateActivity.putExtra("PROJECT_NAME", project_name);
                mContext.startActivity(taskCreateActivity);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.task_fragment_one, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar4);
        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_task_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        spinner.setVisibility(View.VISIBLE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            project_id = bundle.getString("project_id");
            team_members = bundle.getString("team_members");
            requester_email = bundle.getString("requester_email");
            project_name = bundle.getString("project_name");

        try{
            Call<TaskResponse> call = apiInterface.doGetListTasks(project_id, requester_email);

            call.enqueue(new Callback<TaskResponse>() {
                @Override
                public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {


                    Log.d("TAG",response.code()+"");
                    Log.d("Test",response.toString());

                    try{
                        data = response.body();
                        passToAdapter = new ArrayList<>();
                        for(TaskResponse.Payload d:data.payload ){
                            TaskDetails tCard = new TaskDetails(d.name, d.task_id, false, d.status, d.project_id);
                            passToAdapter.add(tCard);
                        }

                    }
                    catch (Exception e){
                        Log.d("Tag", e.getMessage());
                    }
                    spinner.setVisibility(View.INVISIBLE);
                    tlAdapter = new TaskListAdapter(mContext, passToAdapter,requester_email);
//                    adapter = new CustomAdapter(passToAdapter,mContext);
                    recyclerView.setAdapter(tlAdapter);

                }

                @Override
                public void onFailure(Call<TaskResponse> call, Throwable t) {
                    call.cancel();
                    spinner.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            spinner.setVisibility(View.INVISIBLE);
            Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
        }

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taskCreateActivity = new Intent(mContext, TaskCreate.class);
                taskCreateActivity.putExtra("PROJECT_ID", project_id);
                taskCreateActivity.putExtra("TEAM_MEMBER", team_members);
                taskCreateActivity.putExtra("REQUESTER_EMAIL", requester_email);
                taskCreateActivity.putExtra("PROJECT_NAME", project_name);
                mContext.startActivity(taskCreateActivity);
                }
            });

        return view;
    }
}
