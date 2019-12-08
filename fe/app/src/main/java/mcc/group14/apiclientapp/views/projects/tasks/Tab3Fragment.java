package mcc.group14.apiclientapp.views.projects.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.aditya.filebrowser.FileChooser;
import com.aditya.filebrowser.Constants;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.ProjectAttachmentsResponse;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.data.TaskDetails;
import mcc.group14.apiclientapp.data.TaskResponse;
import mcc.group14.apiclientapp.data.UploadAttachmentResponse;
import mcc.group14.apiclientapp.views.users.LoginActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tab3Fragment extends Fragment {

    private Context mContext;
    public String project_id="";
    private Uri fileUri;
    private String filePath;
    private static RecyclerView recyclerView;
    private static APIInterfaceJava apiInterface;
    private static ProjectAttachmentsResponse data;
    private static ArrayList<TaskAttachmentCard> passToAdapter;
    TaskAttachmentAdapter tlAdapter;


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

    public FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_fragment_three, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_attachment_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fab = (FloatingActionButton) view.findViewById(R.id.faButtonAttachments);
        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            project_id = bundle.getString("project_id");


            try {
                Call<ProjectAttachmentsResponse> call = apiInterface.getAllAttachments(project_id);

                call.enqueue(new Callback<ProjectAttachmentsResponse>() {
                    @Override
                    public void onResponse(Call<ProjectAttachmentsResponse> call, Response<ProjectAttachmentsResponse> response) {


                        Log.d("TAG", response.code() + "");

                        try {
                            data = response.body();
                            passToAdapter = new ArrayList<>();
                            for (ProjectAttachmentsResponse.Payload d : data.payload) {
                                TaskAttachmentCard tCard = new TaskAttachmentCard();
                                tCard.filename = d.filename;
                                tCard.fileurl = d.attachment_url;
                                passToAdapter.add(tCard);
                            }

                        } catch (Exception e) {
                            Log.d("Tag", e.getMessage());
                        }

                        tlAdapter = new TaskAttachmentAdapter(mContext, passToAdapter);
//                    adapter = new CustomAdapter(passToAdapter,mContext);
                        recyclerView.setAdapter(tlAdapter);

                    }

                    @Override
                    public void onFailure(Call<ProjectAttachmentsResponse> call, Throwable t) {
                        call.cancel();

                    }
                });
            } catch (Exception e) {

            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i2 = new Intent(mContext, FileChooser.class);
                    i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                    startActivityForResult(i2, 8974);
                }
            });

            //============Getting the bundle values------------------



            Log.d("project_id", project_id);


            //======================================================





        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 8974 && data!=null) {
            if (resultCode == -1) {
                Uri uri = data.getData();
                File file = new File(uri.getPath());

                Log.d("File name",getFileName(uri));


                //=====API CALL====
                APIInterfaceJava apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

                //========Handle File=================================
                Log.d("project_id",project_id);

                Map<String, RequestBody> map = new HashMap<>();
                map.put("project_id", toRequestBody(project_id));


                RequestBody fileBody = RequestBody.create(MediaType.parse(getMimeType(uri)), file);
                map.put("file\"; filename=\""+getFileName(uri), fileBody);
                Log.d("MAP",fileBody.toString());

                //====================================================

                Call<UploadAttachmentResponse> call = apiInterface.uploadAttachment(map);
                call.enqueue(new Callback<UploadAttachmentResponse>() {
                    @Override
                    public void onResponse(Call<UploadAttachmentResponse> call, Response<UploadAttachmentResponse> response) {

                        //start the intent
                        UploadAttachmentResponse data = response.body();
                        Log.d("PROJECT CREATE",response.code()+"");
                        Log.d("TAG",data+"");


                        if(response.code()==201){
                            Log.d("Response","201");

                            MDToast mdToast = MDToast.makeText(mContext, "Attachment Uploaded Successfully", 3, MDToast.TYPE_SUCCESS);
                            mdToast.show();


                        }

                    }

                    @Override
                    public void onFailure(Call<UploadAttachmentResponse> call, Throwable t) {
                        call.cancel();
                    }
                });



            }

                /*
                RequestBody fileBody = RequestBody.create(MediaType.parse(getMimeType(uri)), file);
                map.put("file\"; filename=\"some_file_name.jpg", fileBody);
                Log.d("MAP",fileBody.toString());*/

        }
    }


    // This method  converts String to RequestBody
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }


    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = mContext.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}
