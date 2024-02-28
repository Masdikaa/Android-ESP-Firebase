package com.masdika.espandroidfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: ArrayList<HistoryDataClass>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status: TextView = itemView.findViewById(R.id.tv_status_history)
        val dateTime: TextView = itemView.findViewById(R.id.date_time_history)
        val geoPoint: TextView = itemView.findViewById(R.id.geo_point_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.status.text = currentItem.status
        holder.dateTime.text = currentItem.dateTime
        holder.geoPoint.text = currentItem.geoPoint
    }

}