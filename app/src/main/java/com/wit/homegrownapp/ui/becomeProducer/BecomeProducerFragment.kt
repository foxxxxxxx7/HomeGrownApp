package com.wit.homegrownapp.ui.becomeProducer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wit.homegrownapp.R

class BecomeProducerFragment : Fragment() {

    companion object {
        fun newInstance() = BecomeProducerFragment()
    }

    private lateinit var viewModel: BecomeProducerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_become_producer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BecomeProducerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}