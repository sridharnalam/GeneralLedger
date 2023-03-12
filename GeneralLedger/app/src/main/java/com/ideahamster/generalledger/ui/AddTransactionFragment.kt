package com.ideahamster.generalledger.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.data.entity.Transaction
import com.ideahamster.generalledger.data.util.DateUtil
import com.ideahamster.generalledger.databinding.FragmentAddTransactionBinding
import com.ideahamster.generalledger.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Screen to add a new transaction to the list
 */
@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchCredit.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.switchCredit.text =
                getString(if (isChecked) R.string.credit_label else R.string.debit_label)
        }
        val calender = Calendar.getInstance()

        val timePickerDialogListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                binding.etTime.setText(getString(R.string.time_format, hourOfDay, minute, 0))
            }
        binding.etTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                timePickerDialogListener,
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding.etDate.setText(getString(R.string.date_format, year, month + 1, dayOfMonth))
            }
        binding.etDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                datePickerDialogListener,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCurrency.adapter = adapter
        }

        binding.buttonSave.setOnClickListener {
            if (hasValidInputs()) {
                val transaction = Transaction(
                    null,
                    binding.etAmount.text.toString().trim().toDoubleOrNull(),
                    binding.spinnerCurrency.selectedItem as String,
                    binding.etDescription.text.toString().trim(),
                    binding.switchCredit.isChecked,
                    DateUtil.toSourceDateString(
                        getString(
                            R.string.concat_date_time,
                            binding.etDate.text.toString(),
                            binding.etTime.text.toString()
                        )
                    )
                )
                viewModel.insertTransactionDetails(transaction)
                findNavController().navigate(R.id.action_AddTransactionFragment_to_TransactionListFragment)
            } else {
                Snackbar.make(binding.root, R.string.cannot_be_empty, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun hasValidInputs() =
        !(TextUtils.isEmpty(
            binding.etAmount.text.toString().trim()
        ) || TextUtils.isEmpty(
            binding.etDate.text.toString().trim()
        ) || TextUtils.isEmpty(binding.etTime.text.toString().trim()))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}