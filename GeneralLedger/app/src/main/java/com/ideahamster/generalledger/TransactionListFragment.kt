package com.ideahamster.generalledger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ideahamster.generalledger.databinding.FragmentTransactionListBinding
import com.ideahamster.generalledger.network.NetworkResult
import com.ideahamster.generalledger.ui.adapter.TransactionAdapter
import com.ideahamster.generalledger.ui.adapter.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        binding.bottomSheet.bottomSheetHeader.setOnClickListener {
            bottomSheetBehavior.state =
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_EXPANDED
                else
                    BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bottomSheet.bottomSheetArrow.rotation = slideOffset * 180
            }
        })
        viewModel.transactionResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.progressbar.isVisible = it.isLoading
                }

                is NetworkResult.Failure -> {
                    // Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, it.errorMessage)
                    binding.progressbar.isVisible = false
                }

                is NetworkResult.Success -> {
                    transactionAdapter.updateTransactionList(it.data)
                    binding.progressbar.isVisible = false
                }
                else -> {}
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
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                        return true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}