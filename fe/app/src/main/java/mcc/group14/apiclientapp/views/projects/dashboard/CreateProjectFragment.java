package mcc.group14.apiclientapp.views.projects.dashboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.Project;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.data.TaskCreateResponse;
import mcc.group14.apiclientapp.data.TaskMembers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateProjectFragment extends Fragment implements View.OnClickListener {

    private Context mContext;

    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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


    DatePickerDialog picker;

    EditText txtDeadline;

    EditText txtProjectName;

    EditText txtDescription;

    //EditText txtTeamMembers;

    Button btnCreateProj;
    Spinner spinner;

    NachoTextView nachoTextView;

    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);

        spinner = (Spinner) view.findViewById(R.id.projects_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.project_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        spinner.setAdapter(adapter);
        spinner.setPrompt("Personal");

        //=================Chip



        nachoTextView = (NachoTextView) view.findViewById(R.id.nacho_keywords);

        nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);




        //======Date picker===================

        txtDeadline=(EditText) view.findViewById(R.id.txtdeadline);
        txtDeadline.setInputType(InputType.TYPE_NULL);
        txtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtDeadline.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //==============================Getting all the values

        btnCreateProj = (Button) view.findViewById(R.id.btnCreateProj);
        txtProjectName = (EditText) view.findViewById(R.id.txtInputName);
        txtDescription = (EditText) view.findViewById(R.id.txtDesc);
        imageView = (ImageView) view.findViewById(R.id.imageViewBadge);
        //txtTeamMembers = (EditText) view.findViewById(R.id.txtTeammembers);


        //===============Badge ====================================
        imageView.setOnClickListener(new View.OnClickListener() {
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



        //========================================================


        btnCreateProj.setOnClickListener(this);


        //============================

        return view;


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
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
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        List<String> keywords = nachoTextView.getChipAndTokenValues();

        if(keywords.size() > 3){
            MDToast mdToast = MDToast.makeText(mContext, "You can define at most 3 keywords", 4, MDToast.TYPE_ERROR);
            mdToast.show();
            return;
        }

        Project p = new Project();
        p.name = txtProjectName.getText().toString();
        p.description = txtDescription.getText().toString();

        if(p.name.equals("")|| p.description.equals("")){
            MDToast mdToast = MDToast.makeText(mContext, "Project name and description is mandatory", 4, MDToast.TYPE_ERROR);
            mdToast.show();
            return;
        }

        p.project_type = spinner.getSelectedItem().toString();
        String deadlineInput = txtDeadline.getText().toString();

        //0================Date conversion======================
        String dateArray[] = deadlineInput.split("/");
        if(dateArray.length == 3)
            p.deadline = dateArray[2]+"-"+("00"+dateArray[1]).substring(dateArray[1].length())+"-"+("00"+dateArray[0]).substring(dateArray[0].length())+"T23:59:59";

        //=======================================================
        p.requester_email = firebaseAuth.getCurrentUser().getEmail();
        p.team_members = p.requester_email;
        //========================================================
        if(keywords.size() > 0)
           p.keywords = android.text.TextUtils.join(",", keywords);

        //==========================================================

        Log.d("Project",p.name+" "+p.description+" "+p.project_type+" "+p.deadline+" "+p.team_members+" "+keywords.toString());

        Log.d("ImageView",hasImage(imageView)+" "+imageView.getDrawable().toString());


        //=====API CALL====
        APIInterfaceJava apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);
        Call<ProjectCreateResponse> call = apiInterface.createProject(p);
        call.enqueue(new Callback<ProjectCreateResponse>() {
            @Override
            public void onResponse(Call<ProjectCreateResponse> call, Response<ProjectCreateResponse> response) {

                //start the intent
                ProjectCreateResponse data = response.body();
                Log.d("PROJECT CREATE",response.code()+"");
                Log.d("TAG",data+"");


                if(response.isSuccessful()){
                    Log.d("Response","201");

                    MDToast mdToast = MDToast.makeText(mContext, "Project Created Successfully", 3, MDToast.TYPE_SUCCESS);
                    mdToast.show();


                }

            }

            @Override
            public void onFailure(Call<ProjectCreateResponse> call, Throwable t) {
                call.cancel();
            }
        });
        


    }

    private boolean hasImage(ImageView view) {

        return !(view.getDrawable() instanceof VectorDrawable);
    }
}