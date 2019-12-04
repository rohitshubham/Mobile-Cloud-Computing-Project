package mcc.group14.apiclientapp.views.projects

import ir.mirrajabi.searchdialog.core.Searchable

class SearchUser(private var mTitle: String?): Searchable{

    override fun getTitle(): String {
        return mTitle!!
    }

}