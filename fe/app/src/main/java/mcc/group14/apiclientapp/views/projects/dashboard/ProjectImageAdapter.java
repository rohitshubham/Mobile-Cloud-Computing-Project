package mcc.group14.apiclientapp.views.projects.dashboard;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import mcc.group14.apiclientapp.R;

public class ProjectImageAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProjectImageCard> arrayList;
    public ProjectImageAdapter(Context context, ArrayList<ProjectImageCard> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public  View getView(int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_list_for_projects, parent, false);
        }
        ImageView imageView;
        imageView = (ImageView) convertView.findViewById(R.id.projectImage);
        Picasso.with(context).load(arrayList.get(position).fileurl).into(imageView);
        Log.d("Piccaso",arrayList.get(position).fileurl);
        //imageView.setImageResource(arrayList.get(position).mThumbIds);
        return convertView;
    }
}