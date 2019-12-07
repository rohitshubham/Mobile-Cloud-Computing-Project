package mcc.group14.apiclientapp.views.projects.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;


import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.ProjectsDeleteResponse;
import mcc.group14.apiclientapp.views.projects.tasks.TaskDashboard;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        CustomAdapter thisAdapter = this;

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

                APIInterfaceJava downloadService = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

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
                                Log.d("OptionMenu","Project Description Clicked");
                                return true;
                            case R.id.generateProjectReport:
                                Log.d("OptionMenu","PDF Clicked");

                                Call<ResponseBody> call = downloadService.doGetProjectsReportPDF(dataSet.get(listPosition).project_id);

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("File", "server contacted and has file");

                                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),dataSet.get(listPosition).projectName+".pdf");
                                            if(writtenToDisk){
                                                MDToast mdToast = MDToast.makeText(currContext,"Project Report Downloaded", 3, MDToast.TYPE_SUCCESS);
                                                mdToast.show();
                                            }else{
                                                MDToast mdToast = MDToast.makeText(currContext,"Couldn't Generate Project Report", 3, MDToast.TYPE_ERROR);
                                                mdToast.show();
                                            }
                                            Log.d("File", "file download was a success? " + writtenToDisk);
                                        } else {
                                            Log.d("File", "server contact failed");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        MDToast mdToast = MDToast.makeText(currContext,"Something went wrong!", 3, MDToast.TYPE_ERROR);
                                        mdToast.show();
                                    }
                                });
                                //==========Generate Done===========
                                return true;


                            case R.id.deleteProject:
                                new AlertDialog.Builder(currContext)
                                        .setTitle("Warning")
                                        .setMessage("Do you really want to delete \n\""+dataSet.get(listPosition).projectName+"\" Project?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Log.d("Alert",String.valueOf(whichButton));

                                                makeDeleteRequest(thisAdapter,listPosition,downloadService,dataSet.get(listPosition).project_id,dataSet.get(listPosition).projectName);


                                                //-------------------------------------------------------------
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
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

    public void makeDeleteRequest(CustomAdapter adapter,int listPos,APIInterfaceJava apiInt,String project_id,String projectName){
        //==================Send Delete Request==========================
        try {
            Call<ProjectsDeleteResponse> call = apiInt.doDeleteProject(project_id);

            call.enqueue(new Callback<ProjectsDeleteResponse>() {
                @Override
                public void onResponse(Call<ProjectsDeleteResponse> call, Response<ProjectsDeleteResponse> response) {
                    if (response.body().success.equals("true")) {
                        MDToast mdToast = MDToast.makeText(currContext, "Project " + projectName + " Deleted", 3, MDToast.TYPE_INFO);
                        mdToast.show();

                        adapter.dataSet.remove(listPos);
                        adapter.notifyItemRemoved(listPos);
                        adapter.notifyItemRangeChanged(listPos, dataSet.size());

                        Log.d("Dataset Size",String.valueOf((adapter.getItemCount())));

                    }
                    if (response.body().success.equals("false")) {
                        MDToast mdToast = MDToast.makeText(currContext, "Error: " + response.body().error, 3, MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                }

                @Override
                public void onFailure(Call<ProjectsDeleteResponse> call, Throwable t) {
                    MDToast mdToast = MDToast.makeText(currContext, "Something went wrong!", 3, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
        }
    }



    private boolean writeResponseBodyToDisk(ResponseBody body,String filename) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)  + File.separator +filename );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("File download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}