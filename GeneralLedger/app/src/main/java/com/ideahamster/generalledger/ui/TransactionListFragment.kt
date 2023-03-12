package com.ideahamster.generalledger.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.databinding.FragmentTransactionListBinding
import com.ideahamster.generalledger.network.NetworkResult
import com.ideahamster.generalledger.ui.adapter.BalanceAdapter
import com.ideahamster.generalledger.ui.adapter.TransactionAdapter
import com.ideahamster.generalledger.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Main screen to show the list of transactions
 */
@AndroidEntryPoint
class TransactionListFragment : Fragment() {
    companion object {
        const val TAG = "TransactionListFragment"
    }

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var viewModel: TransactionViewModel

    @Inject
    lateinit var transactionAdapter: TransactionAdapter

    @Inject
    lateinit var balanceAdapter: BalanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetLayout);
        binding.rvTransactions.adapter = transactionAdapter
        binding.bottomSheet.rvCurrencyBalance.adapter = balanceAdapter
        binding.bottomSheet.bottomSheetHeader.setOnClickListener {
            bottomSheetBehavior.state =
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_EXPANDED
                else
                    BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bottomSheet.bottomSheetArrow.rotation = slideOffset * 180
            }
        })

        binding.buttonRefresh.setOnClickListener {
            binding.progressbar.isVisible = true
            binding.containerRefresh.visibility = View.GONE
            viewModel.pullRemoteTransactionList()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTransactionList()
                viewModel.transactionsFlow.collect { transactionList ->
                    if (transactionList.isNotEmpty()) {
                        transactionAdapter.updateTransactionList(transactionList)
                        binding.containerRefresh.visibility = View.GONE
                        binding.progressbar.isVisible = false
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.balanceFlow.collect { balanceList ->
                    if (balanceList.isNotEmpty()) {
                        balanceAdapter.updateBalanceList(balanceList)
                    }
                }
            }
        }

        if (!viewModel.areTransactionsPulled()) {
            viewModel.pullRemoteTransactionList()
            viewModel.networkResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is NetworkResult.Loading -> {
                        binding.progressbar.isVisible = it.isLoading
                    }

                    is NetworkResult.Failure -> {
                        Log.e(TAG, it.errorMessage)
                        binding.progressbar.isVisible = false
                        binding.containerRefresh.visibility = View.VISIBLE
                        binding.tvErrorMessage.text =
                            getString(R.string.error_message_format, it.errorMessage)
                    }

                    is NetworkResult.Success -> {
                        binding.progressbar.isVisible = false
                        binding.containerRefresh.visibility = View.GONE
                        viewModel.markTransactionsPulled()
                    }
                    else -> {}
                }
            }
        }

    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_transaction_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        findNavController().navigate(R.id.action_TransactionListFragment_to_AddTransactionFragment)
                        return true
                    }
                    R.id.action_export -> {
                        exportToCsvFile()
                        return true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun exportToCsvFile() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.exportCSV(transactionAdapter.differ.currentList)
                .collect {
                    val file = File(requireContext().filesDir, it)
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().applicationContext.packageName + ".provider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "text/csv")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(intent);
                    } else {
                        Snackbar.make(
                            binding.root,
                            "CSV file exported to ${file.absolutePath}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}