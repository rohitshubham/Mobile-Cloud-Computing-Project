package layout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.data.UserSearch


class SearchAdapter(var users: MutableList<UserSearch>?,
                    var context: Context):
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(parent.context).
            inflate(R.layout.user_search_recycler_item, parent, false)

        return MyViewHolder.create(view)
    }

    override fun getItemCount(): Int {
        return users.orEmpty().size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.displeyName.text =
            users.orEmpty()[position].display_name
    }

}

class MyViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){

    lateinit var displeyName: TextView

    companion object{

        fun create(iV: View): MyViewHolder {
            var mVH = MyViewHolder(iV)
            mVH.displeyName = iV.findViewById(R.id.display_name)
            return mVH
        }
    }
}

