package com.example.wordsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.databinding.FragmentWordListBinding


class WordListFragment : Fragment() {

    companion object {
        const val LETTER = "letter"
        const val SEARCH_PREFIX = "https://www.google.com/search?q="
    }

    private var _binding:FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView:RecyclerView

    private lateinit var letter:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //getting the values from the safeArgs() passed by the previous fragment.
        arguments?.let {
            letter = it.getString(LETTER).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        _binding = FragmentWordListBinding.inflate(layoutInflater,container,false)

        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.WordList

        // to get intent data in fragment
        // 'activity?.' is to be placed before the intent.extras.getString()
        //val letterId = activity?.intent?.extras?.getString(LETTER).toString()

        recyclerView.adapter = WordAdapter(letter,this.requireContext())
        // Adds a [DividerItemDecoration] between items
        recyclerView.addItemDecoration(
            DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}