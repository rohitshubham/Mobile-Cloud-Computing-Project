package mcc.group14.apiclientapp.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.data.UserProject

class ProjectListAdapter(private val context: Context,
                         private val dataSource: ArrayList<UserProject>): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View{
        val rowView = inflater.inflate(R.layout.project_list_row,
            parent, false)

        // Get title element
        val titleTextView = rowView.findViewById(R.id.project_list_title)
                as TextView

        // TODO: ++ substitute with last_modified
        // Get subtitle element
        //val subtitleTextView = rowView.findViewById(R.id.project_list_subtitle)
        //        as TextView

        // Project image
        val thumbnailImageView = rowView.findViewById(R.id.project_list_image)
                as ImageView

        // 1
        val project = getItem(position) as UserProject

        // 2
        titleTextView.text = project.project_name
        // subtitleTextView.text = project.description

        // TODO: ++ add images management
        // 3
        // Picasso.with(context).load(recipe.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }


}