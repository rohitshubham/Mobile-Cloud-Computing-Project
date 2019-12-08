package mcc.group14.apiclientapp.views.projects.tasks;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.GridView;

import com.aditya.filebrowser.FileChooser;
import com.aditya.filebrowser.Constants;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.ProjectAttachmentsResponse;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.data.ProjectImagesResponse;
import mcc.group14.apiclientapp.data.TaskDetails;
import mcc.group14.apiclientapp.data.TaskResponse;
import mcc.group14.apiclientapp.data.UploadAttachmentResponse;
import mcc.group14.apiclientapp.data.UploadImageResponse;
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectImageAdapter;
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectImageCard;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class Tab2Fragment extends Fragment {

    int image[] = {R.drawable.ic_android_black_24dp,
            R.drawable.ic_black_file,
            R.drawable.ic_file_download,
            R.drawable.ic_folder_open_black_24dp,
            R.drawable.ic_security_black_24dp,
            R.drawable.ic_content_copy_black_24dp,
           };

    private Context mContext;
    public String project_id = "";
    private Uri fileUri;
    private String filePath;
    private static RecyclerView recyclerView;
    private static APIInterfaceJava apiInterface;
    private static ProjectImagesResponse data;

    GridView gridView;
    ArrayList<ProjectImageCard> arrayList;



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

        View view = inflater.inflate(R.layout.task_fragment_two, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.faAddProjectImage);
        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);
        gridView = (GridView) view.findViewById(R.id.projectImageGridView);

        Bundle bundle = this.getArguments();
        if (bundle != null)
            project_id = bundle.getString("project_id");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose a project Badge");

                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo")) {
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);

                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        //=====================Populating image============================

        try {
            Call<ProjectImagesResponse> call = apiInterface.getAllImages(project_id);

            call.enqueue(new Callback<ProjectImagesResponse>() {
                @Override
                public void onResponse(Call<ProjectImagesResponse> call, Response<ProjectImagesResponse> response) {


                    Log.d("TAG", response.code() + "");

                    try {
                        data = response.body();



                        arrayList = new ArrayList<>();
                        for (ProjectImagesResponse.Payload d : data.payload) {
                            ProjectImageCard imageCard = new ProjectImageCard();
                            imageCard.filename = d.filename;
                            imageCard.fileurl = d.attachment_url;
                            Log.d("Tag", imageCard.fileurl);
                            arrayList.add(imageCard);
                        }

                    } catch (Exception e) {
                        Log.d("Tag", e.getMessage());
                    }

                    ProjectImageAdapter adpter= new ProjectImageAdapter(mContext, arrayList);
                    gridView.setAdapter(adpter);

                }

                @Override
                public void onFailure(Call<ProjectImagesResponse> call, Throwable t) {
                    call.cancel();

                }
            });
        } catch (Exception e) {

        }




        //item click listner
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d("Download","Download Photo");
            }
        });

        //=================================================================


        return view;
    }

    // This method  converts String to RequestBody
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");


                        //================API CALL============================

                        Map<String, RequestBody> map = new HashMap<>();
                        map.put("project_id", toRequestBody(project_id));

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.PNG, 100/*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        String file_name = project_id+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+".png";
                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), bitmapdata);
                        map.put("file\"; filename=\""+file_name, fileBody);
                        Log.d("MAP",fileBody.toString());

                        uploadImageFunction(map);




                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = mContext.getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                cursor.close();

                                Map<String, RequestBody> map = new HashMap<>();
                                map.put("project_id", toRequestBody(project_id));

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100/*ignored for PNG*/, bos);
                                byte[] bitmapdata = bos.toByteArray();
                                String file_name = project_id+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+".png";
                                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), bitmapdata);
                                map.put("file\"; filename=\""+file_name, fileBody);
                                Log.d("MAP",fileBody.toString());

                                uploadImageFunction(map);
                            }
                        }

                    }
                    break;
            }
        }
    }

    public void uploadImageFunction(Map<String,RequestBody> map){
        //====================================================

        Call<UploadImageResponse> call = apiInterface.uploadImage(map);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {

                //start the intent
                UploadImageResponse data = response.body();
                Log.d("IMAGE CREATE",response.code()+"");
                Log.d("TAG",data+"");


                if(response.code()==201){
                    Log.d("Response","201");

                    MDToast mdToast = MDToast.makeText(mContext, "Image Uploaded Successfully", 3, MDToast.TYPE_SUCCESS);
                    mdToast.show();


                }

            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                call.cancel();
            }
        });

    }
}










