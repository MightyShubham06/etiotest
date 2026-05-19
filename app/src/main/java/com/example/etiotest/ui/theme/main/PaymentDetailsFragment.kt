package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.etiotest.R
import com.example.etiotest.data.localdb.SessionManager
import com.example.etiotest.databinding.FragmentPaymentDetailsBinding
import com.example.etiotest.data.viewmodel.CartViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentDetailsFragment : Fragment() {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentDetailsFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by activityViewModels()

    private var finalTotalAmount: Int = 0
    private var testIdsJson: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        if (args.directTestId != null) {
            showDirectTestData()
        } else {
            observeCartItems()
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnProceed.setOnClickListener {
            proceedToPayment()
        }
    }


    private fun showDirectTestData() {
        binding.llTestsListContainer.removeAllViews()

        val row = layoutInflater.inflate(R.layout.item_payment_test_row, binding.llTestsListContainer, false)
        row.findViewById<TextView>(R.id.tvTestName).text = args.directTestName
        row.findViewById<TextView>(R.id.tvTestPrice).text = "Rs. ${args.directTestPrice}"
        binding.llTestsListContainer.addView(row)

        finalTotalAmount = args.directTestPrice
        binding.totalFare.text = "Rs. $finalTotalAmount"

        binding.llPriceBreakdownContainer.removeAllViews()
        addPriceRow("Subtotal", "Rs. $finalTotalAmount")

        testIdsJson = JSONArray(listOf<String>(args.directTestId!!)).toString()
    }






    private fun proceedToPayment() {

        if (finalTotalAmount <= 0) {
            Toast.makeText(requireContext(), "Invalid amount!", Toast.LENGTH_SHORT).show()
            return
        }

        if (testIdsJson.isEmpty()) {
            Toast.makeText(requireContext(), "No tests selected!", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionManager = SessionManager(requireContext())

        val orderId      = args.orderId
        val amount       = String.format("%.2f", finalTotalAmount.toDouble())
        val customerId   = args.patientId
        val customerEmail = sessionManager.getUserEmail()
        val customerPhone = sessionManager.getUserPhone()
        val addressId    = args.addressId
        val patientName  = args.patientName
        val bookingDate  = args.bookingDate
        val timeSlot     = args.timeSlot
        val testIds      = testIdsJson

        val debugMessage = """
        Order ID: $orderId
        Amount: Rs. $amount
        Patient ID: $customerId
        Patient Name: $patientName
        Email: $customerEmail
        Phone: $customerPhone
        Address ID: $addressId
        Date: $bookingDate
        Slot: $timeSlot
        Test IDs: $testIds
    """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Payment Data (Testing Purpose)")
            .setMessage(debugMessage)
            .setPositiveButton("Proceed") { dialog, _ ->
                Toast.makeText(requireContext(), "Proceeding to Payment SDK...", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        Log.d("PAYMENT_READY", debugMessage)
    }

    private fun setupUI() {
        binding.patientName.text = args.patientName

        val formattedDate = formatDate(args.bookingDate)
        val formattedSlot = formatSlot(args.timeSlot)
        binding.pickupDateTime.text = "$formattedDate, $formattedSlot"
    }

    private fun observeCartItems() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (args.directTestId != null) return@observe

            binding.llTestsListContainer.removeAllViews()
            var subtotal = 0

            items?.forEach { test ->
                val row = layoutInflater.inflate(R.layout.item_payment_test_row, binding.llTestsListContainer, false)
                row.findViewById<TextView>(R.id.tvTestName).text = test.name

                val finalPrice = test.displayPrice ?: test.price ?: 0
                row.findViewById<TextView>(R.id.tvTestPrice).text = "Rs. $finalPrice"

                binding.llTestsListContainer.addView(row)
                subtotal += finalPrice
            }

            testIdsJson = JSONArray(items?.map { it.id } ?: emptyList<String>()).toString()
            finalTotalAmount = subtotal
            binding.totalFare.text = "Rs. $finalTotalAmount"

            binding.llPriceBreakdownContainer.removeAllViews()
            addPriceRow("Subtotal", "Rs. $subtotal")
        }
    }

    private fun addPriceRow(label: String, value: String) {
        val row = layoutInflater.inflate(
            R.layout.item_price_row,
            binding.llPriceBreakdownContainer,
            false
        )
        row.findViewById<TextView>(R.id.tvLabel).text = label
        row.findViewById<TextView>(R.id.tvValue).text = value
        binding.llPriceBreakdownContainer.addView(row)
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            output.format(input.parse(dateStr)!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun formatSlot(slot: String): String {
        return try {
            val parts = slot.split("-")
            val input = SimpleDateFormat("HH:mm", Locale.getDefault())
            val output = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val start = output.format(input.parse(parts[0])!!)
            val end = output.format(input.parse(parts[1])!!)
            "$start - $end"
        } catch (e: Exception) {
            slot
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}