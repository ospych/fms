package com.example.neofin.ui.journal.neolabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neofin.R
import com.example.neofin.adapters.JournalAdapter
import com.example.neofin.retrofit.RetrofitBuilder
import com.example.neofin.retrofit.data.journal.JournalItem
import com.example.neofin.utils.logs
import kotlinx.android.synthetic.main.fragment_neobis_journal.*
import kotlinx.android.synthetic.main.fragment_neolabs_journal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NeolabsJournalFragment : Fragment(R.layout.fragment_neolabs_journal) {
    private val adapter by lazy { JournalAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (neolabsJournalPB != null) {
            neolabsJournalPB.visibility = View.VISIBLE
        } else {
            logs("Error FilteredJournal, PB")
        }

        setupAdapter()
        getJournalBySection()

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putInt("idJournal", it.id)
            }
            Navigation.findNavController(requireView()).navigate(
                R.id.action_journalFragment_to_journalByIdFragment,
                bundle
            )
        }
    }

    private fun getJournalBySection() = CoroutineScope(Dispatchers.Main).launch {
        val retIn = RetrofitBuilder.getInstance()
        val token = RetrofitBuilder.getToken()
        retIn.getJournalBySection(token, "NEOLABS").enqueue(object :
            Callback<MutableList<JournalItem>> {
            override fun onResponse(
                call: Call<MutableList<JournalItem>>,
                response: Response<MutableList<JournalItem>>
            ) {
                neolabsJournalPB.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    response.body()?.let {
                        adapter.differ.submitList(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    logs("Error in NeolabsJournalFr, getJournal")
                }
            }

            override fun onFailure(call: Call<MutableList<JournalItem>>, t: Throwable) {
                neolabsJournalPB.visibility = View.INVISIBLE
                logs(t.toString())
            }

        })
    }

    private fun setupAdapter() {
        neolabsJournalRV.adapter = adapter
        neolabsJournalRV.layoutManager = LinearLayoutManager(requireContext())
    }
}