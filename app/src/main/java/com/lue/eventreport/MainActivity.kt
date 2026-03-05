package com.lue.eventreport

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lue.eventreport.databinding.ActivityMainBinding
import com.lue.eventreport.ui.adapter.EventAdapter
import com.lue.eventreport.ui.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: EventViewModel by viewModels()
    private val eventAdapter = EventAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            viewModel.predefinedEvents
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eventSpinner.adapter = adapter

        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eventsRecyclerView.adapter = eventAdapter

        binding.sendButton.setOnClickListener {
            val selectedEvent = binding.eventSpinner.selectedItem.toString()
            viewModel.recordEvent(selectedEvent)
            viewModel.sendAllEvents()
        }

        binding.simulateFailCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMockNetworkAlwaysFail(isChecked)
            if (isChecked) {
                binding.statusText.text = "状态：已开启模拟失败模式"
            } else {
                binding.statusText.text = "状态：正常模式"
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventList.observe(this) { events ->
            eventAdapter.submitList(events)
        }

        viewModel.sendResult.observe(this) { result ->
            binding.resultText.text = result
            binding.resultText.visibility = View.VISIBLE
        }

        viewModel.isSending.observe(this) { isSending ->
            binding.sendButton.isEnabled = !isSending
            if (isSending) {
                binding.statusText.text = "状态：正在发送..."
            }
        }
    }
}
