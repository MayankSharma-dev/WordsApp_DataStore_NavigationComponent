package com.example.wordsapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.data.SettingsDataStore
import com.example.wordsapp.databinding.FragmentLetterListBinding
import kotlinx.coroutines.launch

class LetterListFragment : Fragment(), CustomOnClick {


    private var _binding: FragmentLetterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var SettingsDataStore: SettingsDataStore

    private lateinit var recyclerView:RecyclerView

    // Keeps track of which LayoutManager is in use for the [RecyclerView]
    private var isLinearLayoutManager = true

    private lateinit var listener:CustomOnClick

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //To display the options menu, override onCreate().
        // Inside onCreate() call setHasOptionsMenu() passing in true.
        setHasOptionsMenu(true)
    }

    // may be similar to setContentView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // viewBinding
        _binding = FragmentLetterListBinding.inflate(layoutInflater, container, false)

        return binding.root
        // or
        /*
        val view =binding.root
        return view
        */
    }

    //  In onViewCreated(), you would typically bind specific views to properties by calling findViewById().
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.letterList

        // before DataStore
        //chooseLayout()

        /** At the end of the onViewCreated() function, initialize the new variable and pass in the requireContext() to the SettingsDataStore constructor. */
        // Initialize SettingsDataStore
        SettingsDataStore = SettingsDataStore(requireContext())
        SettingsDataStore.preferencesFlow.asLiveData().observe(viewLifecycleOwner) { value ->
            isLinearLayoutManager = value
            chooseLayout()
            // Redraw the menu
            activity?.invalidateMenu()
        }
    }

    private fun chooseLayout() {
        if (isLinearLayoutManager) {
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        } else {
            recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 4)
        }

        recyclerView.adapter = LetterAdapter(this)
    }


    //The only other thing to note is there are some subtle differences with the onCreateOptionsMenu() method
    //when working with fragments. While the Activity class has a global property called menuInflater,
    //Fragment does not have this property. The menu inflater is instead passed into onCreateOptionsMenu().
    //Also note that the onCreateOptionsMenu() method used with fragments doesn't require a return statement
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu,menu)
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
    }


    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
            if (isLinearLayoutManager)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid_layout)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_linear_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {

                isLinearLayoutManager = !isLinearLayoutManager
                chooseLayout()
                setIcon(item)

                // Launch a coroutine and write the layout setting in the preference Datastore
                lifecycleScope.launch {
                    SettingsDataStore.saveLayoutToPreferencesStore(isLinearLayoutManager, requireContext())
                }

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Views are destroyed can created multiple times during the lifecycle of Fragments
        // so you should reset the _binding object so that it does not contains previous values
        _binding = null
    }

    override fun onItemClicked(letters: String) {
        val action = LetterListFragmentDirections.actionLetterListFragmentToWordListFragment(letters)
        findNavController().navigate(action)
    }
}