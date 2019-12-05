package mcc.group14.apiclientapp.views.projects.dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.data.ProjectDetail;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<ProjectCard> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewLastModified;
        TextView textViewProjectType;
        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewLastModified = (TextView) itemView.findViewById(R.id.textViewLastModified);
            this.textViewProjectType = (TextView) itemView.findViewById(R.id.textViewProjectType);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public CustomAdapter(ArrayList<ProjectCard> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_dashboard_cards, parent, false);

        view.setOnClickListener(ProjectDashboard.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView textViewName = holder.textViewName;
        TextView textViewLastModified = holder.textViewLastModified;
        TextView textViewProjectType = holder.textViewProjectType;
        ImageView imageViewIcon = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).projectName);
        String[] dateTime=new String[2];
        if(dataSet.get(listPosition).lastModified != null)
            dateTime  = dataSet.get(listPosition).lastModified.split("T");
        else
            dateTime[0] = "NA";


        //Just showing the date
        textViewLastModified.setText(dateTime[0]);
        textViewProjectType.setText(dataSet.get(listPosition).projectType);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}