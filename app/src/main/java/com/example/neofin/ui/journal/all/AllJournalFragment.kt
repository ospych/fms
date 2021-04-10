package com.example.neofin.ui.journal.all

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neofin.R
import com.example.neofin.adapters.AllJournalAdapter
import com.example.neofin.retrofit.RetrofitBuilder
import com.example.neofin.retrofit.data.journal.AllJournalItem
import com.example.neofin.utils.logs
import kotlinx.android.synthetic.main.fragment_all_journal.*
import kotlinx.android.synthetic.main.fragment_filtered_journal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllJournalFragment : Fragment(R.layout.fragment_all_journal) {
    private val allAdapter by lazy { AllJournalAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allJournalPB != null) {
            allJournalPB.visibility = View.VISIBLE
        } else {
            logs("Error FilteredJournal, PB")
        }

        setupAllAdapter()
        getJournal()

        allAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putInt("idJournal", it.id)
            }
            Navigation.findNavController(requireView()).navigate(
                R.id.action_journalFragment_to_journalByIdFragment,
                bundle
            )
        }
    }

    private fun getJournal() = CoroutineScope(Dispatchers.Main).launch {
        val retIn = RetrofitBuilder.getInstance()
        val token = RetrofitBuilder.getToken()
        retIn.getJournal(token).enqueue(object : Callback<MutableList<AllJournalItem>> {
            override fun onResponse(call: Call<MutableList<AllJournalItem>>, response: Response<MutableList<AllJournalItem>>) {
                if (response.isSuccessful) {
                    allJournalPB.visibility = View.INVISIBLE
                    response.body()?.let {
                        allAdapter.differ.submitList(it)
                        allAdapter.notifyDataSetChanged()
                    }
                } else {
                    logs("Error in AllJournalFr, getJournal")
                }
            }

            override fun onFailure(call: Call<MutableList<AllJournalItem>>, t: Throwable) {
                logs(t.toString())
                allJournalPB.visibility = View.INVISIBLE
            }

        })
    }

    private fun setupAllAdapter() {
        allJournalRV.adapter = allAdapter
        allJournalRV.layoutManager = LinearLayoutManager(requireContext())
    }
}