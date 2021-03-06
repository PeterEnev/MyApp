package com.example.myapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapp.R
import com.example.myapp.models.Country
import kotlinx.android.synthetic.main.list_item_country.view.*

class CountryListAdapter(val context: Context,
                         var listCountry: ArrayList<Country>) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun updateList(newListCountry: ArrayList<Country>){
        listCountry = newListCountry
        notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_country, parent, false)
        val country = getItem(position) as Country

        rowView.countryName.text = country.countryName
        rowView.countryPrefix.text = country.countryPrefix
        return rowView
    }

    override fun getItem(position: Int) = listCountry[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = listCountry.size
}

