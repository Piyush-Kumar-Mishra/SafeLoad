package com.example.safeload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class Fragment1 : Fragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var hydrationProgressBar: ProgressBar
    private lateinit var restProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hydrationProgressBar = view.findViewById(R.id.hydrationProgressBar)
        restProgressBar = view.findViewById(R.id.restProgressBar)

        // Observe hydration progress
        taskViewModel.hydrationProgress.observe(viewLifecycleOwner, Observer { progress ->
            hydrationProgressBar.progress = progress
        })

        // Observe rest progress
        taskViewModel.restProgress.observe(viewLifecycleOwner, Observer { progress ->
            restProgressBar.progress = progress
        })
    }
}
