package mcc.group14.apiclientapp.views.users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.MemberAutocompleteResponse;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.data.UserUpdateResponse;
import mcc.group14.apiclientapp.views.projects.dashboard.MemberAutoCompleteAdapter;
import mcc.group14.apiclientapp.views.projects.dashboard.ProjectsDashboardMainActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class UserProfileActivity extends AppCompatActivity {

    APIInterfaceJava apiInterface;



    Context currContext;

    ImageView profile_pic;

    EditText newPass;
    EditText newPassConf;

    String email_id;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_option);

        currContext = this;

        Intent intent = getIntent();

        // get the info from login/sign-up
        email_id = intent.getStringExtra("USER_EMAIL");

        Toolbar toolbar = findViewById(R.id.usr_set_toolbar);

        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.userProfile_menu)
                {
                  return false;


                }
                else if(item.getItemId()== R.id.logout_menu)
                {
                    Log.d("Logout","Logout in progress");

                    firebaseAuth.signOut();
                }

                return false;
            }
        });

        Button updateProfileBtn = findViewById(R.id.btnUpdateProfile);


        AutoCompleteTextView acText = findViewById(R.id.txtInputMemberSearch);

        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        profile_pic = findViewById(R.id.imageViewUserProfile);
        newPass = findViewById(R.id.usrPass);
        newPassConf = findViewById(R.id.usrPassConf);

        //Init and attach
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);


        updateProfileBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String newPassText = newPass.getText().toString();
                String newPassConfText = newPassConf.getText().toString();

                if(!newPassText.equals("") || !newPassConfText.equals("")){
                    MDToast mdToast = MDToast.makeText(currContext,"Passwords didn't match", 3, MDToast.TYPE_ERROR);
                    if(!newPassText.equals(newPassConfText)){
                        mdToast.show();
                        return;
                    }else {
                        updateProfileAPICall(newPassText);
                    }



                }else{
                    updateProfileAPICall(newPassText);
                }
            }
        });


        //===============Badge ====================================
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
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



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        profile_pic.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = currContext.getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                profile_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    public void updateProfileAPICall(String password){
        //========Handle File=================================



        Map<String, RequestBody> map = new HashMap<>();

        if(!password.equals(""))
            map.put("password", toRequestBody(password));

        map.put("email_id", toRequestBody(email_id));

        if(hasImage(profile_pic)) {

            Bitmap bitmap = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            //File file = new File(bitmapdata);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), bitmapdata);
            map.put("file\"; filename=\"some_file_name.jpg", fileBody);
            Log.d("MAP", fileBody.toString());
        }



        //====================================================

        Call<UserUpdateResponse> call = apiInterface.updateUserInfo(map);
        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {

                UserUpdateResponse data = response.body();
                Log.d("PROJECT CREATE",response.code()+"");
                Log.d("TAG",data+"");

                try {
                    if (response.body().success == "true") {
                        Log.d("Response", "200");

                        MDToast mdToast = MDToast.makeText(currContext, "Profile Updated Successfully", 3, MDToast.TYPE_SUCCESS);
                        mdToast.show();



                    }
                }catch (Exception e){
                    Log.d("Response", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // This method  converts String to RequestBody
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }


    private boolean hasImage(ImageView view) {

        return !(view.getDrawable() instanceof VectorDrawable);
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){
                //Do anything here which needs to be done after signout is complete
                Log.d("Logout","Logout in progress");
                Intent intent = new Intent(currContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
            else {
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

}
