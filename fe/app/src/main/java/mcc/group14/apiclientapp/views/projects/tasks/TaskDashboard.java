package mcc.group14.apiclientapp.views.projects.tasks;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import mcc.group14.apiclientapp.R;

public class TaskDashboard extends AppCompatActivity {
    private TaskAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_dashboard);
        Intent intent  = getIntent();

        String project_id = intent.getStringExtra("PROJECT_ID");
        String project_name = intent.getStringExtra("PROJECT_NAME");
        String team_members = intent.getStringExtra("TEAM_MEMBER");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(project_name);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TaskAdapter(getSupportFragmentManager());

        Tab1Fragment fragment_1 = new Tab1Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("project_id", project_id);
        bundle.putString("team_members", team_members);
        fragment_1.setArguments(bundle);


        adapter.addFragment(fragment_1, "Tasks");
        adapter.addFragment(new Tab2Fragment(), "Images");
        adapter.addFragment(new Tab3Fragment(), "Attachment");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
