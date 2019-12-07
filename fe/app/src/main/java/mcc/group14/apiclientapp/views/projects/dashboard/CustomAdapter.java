package mcc.group14.apiclientapp.views.projects.dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import java.time.Instant;
import java.util.ArrayList;


import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.views.projects.tasks.TaskDashboard;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<ProjectCard> dataSet;
    Context currContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewLastModified;
        TextView textViewProjectType;
        ImageView imageViewIcon;
        TextView buttonViewOption;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewLastModified = (TextView) itemView.findViewById(R.id.textViewLastModified);
            this.textViewProjectType = (TextView) itemView.findViewById(R.id.textViewProjectType);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        }
    }

    public CustomAdapter(ArrayList<ProjectCard> data, Context currContext) {
        this.dataSet = data;
        this.currContext = currContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_dashboard_cards, parent, false);

        //view.setOnClickListener(ProjectsHomeFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView textViewName = holder.textViewName;
        TextView textViewLastModified = holder.textViewLastModified;
        TextView textViewProjectType = holder.textViewProjectType;
        ImageView imageViewIcon = holder.imageViewIcon;
        TextView buttonViewOption = holder.buttonViewOption;

        textViewName.setText(dataSet.get(listPosition).projectName);
        String[] dateTime=new String[2];
        if(dataSet.get(listPosition).lastModified != null)
            dateTime  = dataSet.get(listPosition).lastModified.split("T");
        else
            dateTime[0] = "NA";


        //Just showing the date
        textViewLastModified.setText(dateTime[0]);
        textViewProjectType.setText(dataSet.get(listPosition).projectType);

        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent in = new Intent(currContext, TaskDashboard.class);
                in.putExtra("PROJECT_ID",dataSet.get(listPosition).project_id);
                in.putExtra("PROJECT_NAME",dataSet.get(listPosition).projectName);
                in.putExtra("TEAM_MEMBER",dataSet.get(listPosition).team_member);
                in.putExtra("REQUESTER_EMAIL",dataSet.get(listPosition).requester_email);
                currContext.startActivity(in);
                //Log.d("holder",dataSet.get(listPosition).projectName);

            }
        });

        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent in = new Intent(currContext, TaskDashboard.class);
                in.putExtra("PROJECT_ID",dataSet.get(listPosition).project_id);
                in.putExtra("PROJECT_NAME",dataSet.get(listPosition).projectName);
                in.putExtra("TEAM_MEMBER",dataSet.get(listPosition).team_member);
                in.putExtra("REQUESTER_EMAIL",dataSet.get(listPosition).requester_email);

                currContext.startActivity(in);
                //Log.d("holder",dataSet.get(listPosition).projectName);

            }
        });

        holder.textViewLastModified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent in = new Intent(currContext, TaskDashboard.class);
                in.putExtra("PROJECT_ID",dataSet.get(listPosition).project_id);
                in.putExtra("PROJECT_NAME",dataSet.get(listPosition).projectName);
                in.putExtra("TEAM_MEMBER",dataSet.get(listPosition).team_member);
                in.putExtra("REQUESTER_EMAIL",dataSet.get(listPosition).requester_email);

                currContext.startActivity(in);
                //Log.d("holder",dataSet.get(listPosition).projectName);

            }
        });

        holder.textViewProjectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent in = new Intent(currContext, TaskDashboard.class);
                in.putExtra("PROJECT_ID",dataSet.get(listPosition).project_id);
                in.putExtra("PROJECT_NAME",dataSet.get(listPosition).projectName);
                in.putExtra("TEAM_MEMBER",dataSet.get(listPosition).team_member);
                in.putExtra("REQUESTER_EMAIL",dataSet.get(listPosition).requester_email);

                currContext.startActivity(in);
                //Log.d("holder",dataSet.get(listPosition).projectName);

            }
        });

        //===================Options Menu==========================

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(currContext, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.action_projects);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addMemberOption:
                                Log.d("OptionMenu","Add Member Clicked");
                                return true;
                            case R.id.projectDescription:
                                Log.d("OptionMenu","Proejct Description Clicked");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}