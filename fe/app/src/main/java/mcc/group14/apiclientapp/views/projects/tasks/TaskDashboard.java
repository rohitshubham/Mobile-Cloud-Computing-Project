package mcc.group14.apiclientapp.views.projects.tasks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import mcc.group14.apiclientapp.R;
import mcc.group14.apiclientapp.views.users.LoginActivity;
import mcc.group14.apiclientapp.views.users.UserProfileActivity;

public class TaskDashboard extends AppCompatActivity {
    private TaskAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    Context currContext;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_dashboard);
        Intent intent  = getIntent();

        currContext = this;
        //Init and attach
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        String project_id = intent.getStringExtra("PROJECT_ID");
        String project_name = intent.getStringExtra("PROJECT_NAME");
        String team_members = intent.getStringExtra("TEAM_MEMBER");
        String requester_email = intent.getStringExtra("REQUESTER_EMAIL");

        Toolbar toolbar = (Toolbar) findViewById(R.id.task_dash_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(project_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.userProfile_menu)
                {
                    Intent intent = new Intent(currContext, UserProfileActivity.class);
                    intent.putExtra("USER_EMAIL", requester_email);
                    startActivity(intent);


                }
                else if(item.getItemId()== R.id.logout_menu)
                {
                    Log.d("Logout","Logout in progress");

                    firebaseAuth.signOut();
                }

                return false;
            }
        });



        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TaskAdapter(getSupportFragmentManager());

        Tab1Fragment fragment_1 = new Tab1Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("project_id", project_id);
        bundle.putString("team_members", team_members);
        bundle.putString("requester_email", requester_email);
        bundle.putString("project_name", project_name);
        fragment_1.setArguments(bundle);

        Tab3Fragment fragment_3= new Tab3Fragment();
        Bundle b3 = new Bundle();
        b3.putString("project_id", project_id);
        fragment_3.setArguments(b3);


        Tab2Fragment fragment_2= new Tab2Fragment();
        Bundle b2 = new Bundle();
        b2.putString("project_id", project_id);
        fragment_2.setArguments(b2);


        adapter.addFragment(fragment_1, "Tasks");
        adapter.addFragment(fragment_2, "Images");
        adapter.addFragment(fragment_3, "Attachment");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
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
                currContext.startActivity(intent);
            }
            else {
            }
        }
    };
}
