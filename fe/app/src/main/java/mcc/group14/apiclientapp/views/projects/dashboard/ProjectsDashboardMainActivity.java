package mcc.group14.apiclientapp.views.projects.dashboard;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.api.APIInterfaceJava;
import mcc.group14.apiclientapp.api.ProjectAPIJava;
import mcc.group14.apiclientapp.data.RegistrationToken;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProjectsDashboardMainActivity extends AppCompatActivity {
    private static final String TAG = "MyFirebaseMsgService";
    private static APIInterfaceJava apiInterfaceJava;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_dashboard);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        sendResgistrationTokenToServer(token);
                    }
                });

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_projects,
                    new ProjectsHomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new ProjectsHomeFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new UserSettingsFragment();
                            break;
                        case R.id.nav_create_project:
                            selectedFragment = new CreateProjectFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_projects,
                            selectedFragment).commit();

                    return true;
                }
            };

    private void sendResgistrationTokenToServer(String token){
        String email_id = getIntent().getStringExtra("USER_EMAIL");

        RegistrationToken reg_token = new RegistrationToken();

        reg_token.email_id = email_id;
        reg_token.registration_token = token;
        if(apiInterfaceJava == null) {
            apiInterfaceJava = ProjectAPIJava.getClient().create(APIInterfaceJava.class);
        }
        Call<ResponseBody> call = apiInterfaceJava.sendResgistrationTokenToServer(reg_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "successfully updated the key!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "something went wrong while uploading the key");
            }
        });



    }
}