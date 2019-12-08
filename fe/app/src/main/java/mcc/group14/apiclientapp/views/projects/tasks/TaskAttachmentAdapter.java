package mcc.group14.apiclientapp.views.projects.tasks;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.media.MediaCodecInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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


public class TaskAttachmentAdapter extends RecyclerView.Adapter<TaskAttachmentAdapter.TaskAttachmentList>{

    Context context;
    List<TaskAttachmentCard> list = new ArrayList<>();
    private static TaskAttachmentCard taskAttachment;
    private static APIInterfaceJava apiInterfaceJava;
    private String requester_email;

    public TaskAttachmentAdapter(Context context, List<TaskAttachmentCard> list) {
        this.context = context;
        this.list =   list;
    }

    @Override
    public TaskAttachmentList onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(context).inflate(R.layout.attachment_card, parent,false);

        return new TaskAttachmentList(view);
    }

    @Override
    public void onBindViewHolder(final TaskAttachmentList holder, final int position) {

        taskAttachment = list.get(position);


        holder.filename.setText(taskAttachment.filename);


        apiInterfaceJava =  ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        holder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                APIInterfaceJava downloadService = ProjectAPIJava.getClient().create(APIInterfaceJava.class);
                Call<ResponseBody> call = downloadService.downloadAttachMent(taskAttachment.fileurl);

                Log.d("URL",taskAttachment.fileurl);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("File", "server contacted and has file");

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),taskAttachment.filename);
                            if(writtenToDisk){
                                MDToast mdToast = MDToast.makeText(context,"Attachment Downloaded", 3, MDToast.TYPE_SUCCESS);
                                mdToast.show();
                            }else{
                                MDToast mdToast = MDToast.makeText(context,"Couldn't Download Attachment", 3, MDToast.TYPE_ERROR);
                                mdToast.show();
                            }
                            Log.d("File", "file download was a success? " + writtenToDisk);
                        } else {
                            Log.d("File", "server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        MDToast mdToast = MDToast.makeText(context,"Something went wrong!", 3, MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TaskAttachmentList extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView filename;
        ImageView downloadIcon;

        public TaskAttachmentList(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.imageViewAttachment);
            filename = itemView.findViewById(R.id.attachmentName);
            downloadIcon = itemView.findViewById(R.id.imageViewAttachmentDown);
        }
    }

    public List<TaskAttachmentCard> getTaskDetailsList(){
        return  list;
    }


    private boolean writeResponseBodyToDisk(ResponseBody body,String filename) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)  + File.separator +filename );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[10000];

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


}
