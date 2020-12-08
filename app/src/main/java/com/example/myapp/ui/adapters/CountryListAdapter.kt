package com.example.myapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.myapp.R
import com.example.myapp.models.Country
import kotlinx.android.synthetic.main.list_item_country.view.*

class CountryListAdapter(val context: Context,
                         val listCountry: ArrayList<Country>) : BaseAdapter() {

    val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.list_item_country, parent, false)
        val country = getItem(position) as Country

        rowView.countryName.text = country.countryName
        rowView.countryPrefix.text = country.countryPrefih
        return rowView
    }

    override fun getItem(position: Int): Any {
        return listCountry[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listCountry.size
    }
}

class CountryDiffUtil: DiffUtil.ItemCallback<Country>(){
    override fun areItemsTheSame(
        oldItem: Country,
        newItem: Country
    ): Boolean {
        return oldItem.coutryId == newItem.coutryId
    }

    override fun areContentsTheSame(
        oldItem: Country,
        newItem: Country
    ): Boolean {
        return oldItem == newItem
    }
}

