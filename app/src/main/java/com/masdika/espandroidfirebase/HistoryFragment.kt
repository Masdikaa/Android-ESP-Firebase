package com.masdika.espandroidfirebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masdika.espandroidfirebase.databinding.FragmentHistoryBinding

private var _binding: FragmentHistoryBinding? = null
private val binding get() = _binding!!

class HistoryFragment : Fragment() {

    private lateinit var adapter: HistoryAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyArrayList: ArrayList<HistoryDataClass>

    lateinit var statusId: Array<String>
    lateinit var dateTimeId: Array<String>
    lateinit var geoPointId: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()

        historyRecyclerView = view.findViewById(R.id.recycleView)
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.setHasFixedSize(true)
        historyRecyclerView.adapter = HistoryAdapter(historyArrayList)

    }

    private fun dataInitialize() {
        historyArrayList = arrayListOf<HistoryDataClass>()

        statusId = arrayOf(
            "Normal",
            "Medium Hypothermia",
            "Emergency"
        )

        dateTimeId = arrayOf(
            "30 Januari 2024 | 13.30",
            "30 Januari 2024 | 14.00",
            "30 Januari 2024 | 14.10"
        )

        geoPointId = arrayOf(
            "-7.640658, 111.517252",
            "-7.740658, 112.517252",
            "-7.840658, 113.517252"
        )

        for (i in statusId.indices) {
            val historyData = HistoryDataClass(statusId[i], dateTimeId[i], geoPointId[i])
            historyArrayList.add(historyData)

        }

        //historyRecyclerView.adapter = HistoryAdapter(historyArrayList)

    }


}