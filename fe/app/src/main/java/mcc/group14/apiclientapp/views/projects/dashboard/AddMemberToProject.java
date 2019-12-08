package mcc.group14.apiclientapp.views.projects.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.MemberAutocompleteResponse;
import mcc.group14.apiclientapp.data.ProjectAttachmentsResponse;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.views.projects.tasks.TaskAttachmentAdapter;
import mcc.group14.apiclientapp.views.projects.tasks.TaskAttachmentCard;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberToProject extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private MemberAutoCompleteAdapter autoSuggestAdapter;
    APIInterfaceJava apiInterface;
    MemberAutocompleteResponse data;
    String name,team_members,project_id,requester_email,project_type,creation_time,userEmail,userAuth;
    String[] selected;

    Context currContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_members_to_project);

        currContext = this;

        Intent intent = getIntent();

        Toolbar heading = findViewById(R.id.toolbarAddMember);
        Button addMemberBtn = findViewById(R.id.btnAddMembersProj);
        heading.setTitle(intent.getStringExtra("PROJECT_NAME"));

        //===========Getting extras===================================
        name = intent.getStringExtra("PROJECT_NAME");
        team_members = intent.getStringExtra("PROJECT_MEMBERS");
        project_id = intent.getStringExtra("PROJECT_ID");
        requester_email = intent.getStringExtra("REQ_EMAIL");
        project_type = intent.getStringExtra("PROJ_TYPE");
        creation_time = intent.getStringExtra("CREATION_TIME");
        userEmail =  intent.getStringExtra("USER_EMAIL");
        userAuth =  intent.getStringExtra("USER_AUTH");

        //===============================================================

        AutoCompleteTextView acText = findViewById(R.id.txtInputMemberSearch);

        apiInterface = ProjectAPIJava.getClient().create(APIInterfaceJava.class);

        autoSuggestAdapter = new MemberAutoCompleteAdapter(this,android.R.layout.simple_dropdown_item_1line);

        acText.setThreshold(3);
        acText.setAdapter(autoSuggestAdapter);

        acText.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selected = autoSuggestAdapter.getObject(position).split("-");

                    }
                });
        acText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(acText.getText())) {
                        makeApiCall(acText.getText().toString());
                    }
                }
                return false;
            }
        });

        addMemberBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                addMemberApiCall(selected[1],currContext);


            }
        });



    }

    private void makeApiCall(String text) {
        try {
            Call<MemberAutocompleteResponse> call = apiInterface.getMemberSuggestions(text);

            call.enqueue(new Callback<MemberAutocompleteResponse>() {
                @Override
                public void onResponse(Call<MemberAutocompleteResponse> call, Response<MemberAutocompleteResponse> response) {


                    Log.d("TAG", response.code() + "");

                    try {
                        data = response.body();
                        ArrayList<String> passToAdapter = new ArrayList<>();
                        for (MemberAutocompleteResponse.Payload d : data.payload) {
                          passToAdapter.add(d.display_name+"-"+d.email_id);
                        }
                        autoSuggestAdapter.setData(passToAdapter);
                        autoSuggestAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.d("Tag", e.getMessage());
                    }



                }

                @Override
                public void onFailure(Call<MemberAutocompleteResponse> call, Throwable t) {
                    call.cancel();

                }
            });
        } catch (Exception e) {

        }
    }

    public void addMemberApiCall(String member, Context context){
        //========Handle File=================================

        ArrayList<String> members = new ArrayList<>(Arrays.asList(team_members.split(",")));
        members.add(member);

        String new_members = String.join(",",members);

        Map<String, RequestBody> map = new HashMap<>();
        map.put("name", toRequestBody(name));
        map.put("team_members", toRequestBody(new_members));
        map.put("requester_email", toRequestBody(requester_email));
        map.put("project_type", toRequestBody(project_type));
        map.put("project_id", toRequestBody(project_id));
        map.put("creation_time", toRequestBody(creation_time));

        Log.d("MAP",new_members);

        //====================================================

        Call<ProjectCreateResponse> call = apiInterface.updateProjectWithBadge(map);
        call.enqueue(new Callback<ProjectCreateResponse>() {
            @Override
            public void onResponse(Call<ProjectCreateResponse> call, Response<ProjectCreateResponse> response) {

                //start the intent
                ProjectCreateResponse data = response.body();
                Log.d("PROJECT CREATE",response.code()+"");
                Log.d("TAG",data+"");

                try {
                    if (response.body().success == "true") {
                        Log.d("Response", "200");

                        MDToast mdToast = MDToast.makeText(context, "Member Added Successfully", 3, MDToast.TYPE_SUCCESS);
                        mdToast.show();

                        Intent intent = new Intent(currContext, ProjectsDashboardMainActivity.class);
                        intent.putExtra("USER_EMAIL", userEmail);
                        intent.putExtra("USER_AUTH", userAuth);
                        currContext.startActivity(intent);


                    }
                }catch (Exception e){
                    Log.d("Response", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ProjectCreateResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // This method  converts String to RequestBody
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }
}
