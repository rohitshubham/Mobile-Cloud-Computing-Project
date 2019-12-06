package mcc.group14.apiclientapp.views.projects.tasks;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import mcc.group14.apiclientapp.R;

public class TaskDashboard extends AppCompatActivity {
    private TaskAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_dashboard);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TaskAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Tasks");
        adapter.addFragment(new Tab2Fragment(), "Images");
        adapter.addFragment(new Tab3Fragment(), "Attachment");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
