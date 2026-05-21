package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
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
import `in`.juspay.hypersdk.core.MerchantViewType
import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import `in`.juspay.services.HyperServices
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentDetailsFragment : Fragment() {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentDetailsFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var hyperServices: HyperServices

    private var finalTotalAmount: Int = 0
    private var testIdsJson: String = ""

    // HDFC SmartGateway Credentials
    private val MERCHANT_ID = "SG4808"
    private val CLIENT_ID = "hdfcmaster"
    private val ENVIRONMENT = "sandbox"

    private val SECRET_KEY = "C2D265CCC6745B0A102A7579013BF2"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Juspay HyperServices with current Activity
        hyperServices = HyperServices(requireActivity())

        setupUI()

        // Check if user came from direct test or cart
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

        val row = layoutInflater.inflate(
            R.layout.item_payment_test_row,
            binding.llTestsListContainer,
            false
        )

        row.findViewById<TextView>(R.id.tvTestName).text = args.directTestName
        row.findViewById<TextView>(R.id.tvTestPrice).text = "Rs. ${args.directTestPrice}"

        binding.llTestsListContainer.addView(row)

        finalTotalAmount = args.directTestPrice
        binding.totalFare.text = "Rs. $finalTotalAmount"

        binding.llPriceBreakdownContainer.removeAllViews()
        addPriceRow("Subtotal", "Rs. $finalTotalAmount")

        testIdsJson = JSONArray(listOf(args.directTestId!!)).toString()
    }

    private fun proceedToPayment() {

        // Validate amount
        if (finalTotalAmount <= 0) {
            Toast.makeText(requireContext(), "Invalid amount!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate test selection
        if (testIdsJson.isEmpty()) {
            Toast.makeText(requireContext(), "No tests selected!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Disable button to prevent multiple clicks
            binding.btnProceed.isEnabled = false
            binding.btnProceed.text = "Processing..."

            // Build the initiate payload
            val initiatePayload = JSONObject().apply {
                put("requestId", args.orderId)
                put("service", "in.juspay.hyperpay")
                put("payload", JSONObject().apply {
                    put("action", "initiate")
                    put("merchantId", MERCHANT_ID)
                    put("clientId", CLIENT_ID)
                    put("environment", ENVIRONMENT)
                })
            }

            Log.d("JUSPAY_DEBUG", "STEP 1: Calling Initiate...")
            Log.d("JUSPAY_DEBUG", "Initiate Payload: $initiatePayload")

            // Call initiate - SDK will callback via createJuspayCallback()
            hyperServices.initiate(
                requireActivity(),
                initiatePayload,
                createJuspayCallback()
            )

        } catch (e: Exception) {
            // Reset button if something goes wrong
            binding.btnProceed.isEnabled = true
            binding.btnProceed.text = "Proceed Payment"
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            Log.e("JUSPAY_DEBUG", "Initiate Exception: ${e.message}")
        }
    }

    private fun openPaymentPage() {

        try {

            val sessionManager = SessionManager(requireContext())

            val orderId = args.orderId
            val amount = String.format("%.2f", finalTotalAmount.toDouble())

            val customerId = args.patientId
            val customerEmail = sessionManager.getUserEmail() ?: ""
            val customerPhone = sessionManager.getUserPhone() ?: ""

            // Generate Signature
            val timestamp = System.currentTimeMillis().toString()

            val signature = generateSignature(
                orderId,
                amount,
                timestamp
            )

            Log.d("JUSPAY_DEBUG", "Generated Signature: $signature")

            val orderDetails = JSONObject().apply {

                put("merchant_id", MERCHANT_ID)
                put("order_id", orderId)
                put("amount", amount)
                put("currency", "INR")
                put("timestamp", timestamp)
            }

            // Main Payload
            val innerPayload = JSONObject().apply {

                put("action", "paymentPage")

                put("merchantId", MERCHANT_ID)
                put("clientId", CLIENT_ID)
                put("environment", ENVIRONMENT)

                put("merchantKeyId", "AE05907DC40472BB7E757D4B2EFBFE")

                put("customer_id", customerId)
                put("customer_email", customerEmail)
                put("customer_phone", customerPhone)

                put("first_name", args.patientName)
                put("description", "ETIO Test Booking")

                // IMPORTANT
                put("orderDetails", orderDetails.toString())

                // IMPORTANT
                put("signature", signature)
            }

            val processPayload = JSONObject().apply {

                put("requestId", orderId)
                put("service", "in.juspay.hyperpay")
                put("payload", innerPayload)
            }

            Log.d("JUSPAY_DEBUG", "STEP 2: Calling Process...")
            Log.d("JUSPAY_DEBUG", "Process Payload: $processPayload")

            // Open Juspay Payment Page
            hyperServices.process(
                requireActivity(),
                processPayload
            )

        } catch (e: Exception) {

            resetButton()

            Log.e(
                "JUSPAY_DEBUG",
                "Process Exception: ${e.message}"
            )

            e.printStackTrace()
        }
    }


    private fun generateSignature(
        orderId: String,
        amount: String,
        timestamp: String
    ): String {

        return try {

            val data =
                "$MERCHANT_ID|$orderId|$amount|INR|$timestamp"

            val secretKeySpec = javax.crypto.spec.SecretKeySpec(
                SECRET_KEY.toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )

            val mac = javax.crypto.Mac.getInstance("HmacSHA256")

            mac.init(secretKeySpec)

            val hash = mac.doFinal(
                data.toByteArray(Charsets.UTF_8)
            )

            hash.joinToString("") {
                "%02x".format(it)
            }

        } catch (e: Exception) {

            e.printStackTrace()
            ""
        }
    }

    private fun createJuspayCallback(): HyperPaymentsCallbackAdapter {

        return object : HyperPaymentsCallbackAdapter() {

            override fun onEvent(
                jsonObject: JSONObject,
                responseHandler: JuspayResponseHandler
            ) {
                try {
                    val event = jsonObject.optString("event")

                    Log.d("JUSPAY_DEBUG", "Event Received: $event")
                    Log.d("JUSPAY_DEBUG", "Full Response: $jsonObject")

                    when (event) {

                        // SDK initialization is complete
                        "initiate_result" -> {
                            val isError = jsonObject.optBoolean("error", false)

                            if (!isError) {
                                // Initiate was successful, now open payment page
                                Log.d("JUSPAY_DEBUG", "Initiate SUCCESS -> Opening Payment Page...")
                                openPaymentPage()
                            } else {
                                // Initiate failed - check credentials or network
                                val errorMsg = jsonObject.optString(
                                    "errorMessage",
                                    "SDK initialization failed"
                                )
                                Log.e("JUSPAY_DEBUG", "Initiate FAILED: $errorMsg")
                                Toast.makeText(
                                    requireContext(),
                                    "Payment initialization failed: $errorMsg",
                                    Toast.LENGTH_SHORT
                                ).show()
                                resetButton()
                            }
                        }

                        // Payment flow is complete
                        "process_result" -> {
                            val payload = jsonObject.optJSONObject("payload")
                            val status = payload?.optString("status")
                            val isError = jsonObject.optBoolean("error", false)

                            Log.d("JUSPAY_DEBUG", "Payment Status: $status | Is Error: $isError")

                            // Handle SDK level error (e.g. JP_003 signature missing)
                            if (isError) {
                                val errorCode = jsonObject.optString("errorCode", "")
                                val errorMsg = jsonObject.optString(
                                    "errorMessage",
                                    "Payment failed"
                                )
                                Log.e("JUSPAY_DEBUG", "Payment Error Code: $errorCode | Message: $errorMsg")
                                Toast.makeText(
                                    requireContext(),
                                    "Error: $errorMsg",
                                    Toast.LENGTH_SHORT
                                ).show()
                                resetButton()
                                return
                            }

                            // Handle payment status
                            when (status) {
                                "charged" -> {
                                    // Payment was successful
                                    Log.d("JUSPAY_DEBUG", "Payment SUCCESS!")
                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Successful!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // TODO: Navigate to success screen
                                    // findNavController().navigate(R.id.paymentSuccessFragment)
                                }
                                "backpressed", "user_aborted" -> {
                                    // User cancelled the payment
                                    Log.d("JUSPAY_DEBUG", "Payment CANCELLED by user")
                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Cancelled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    resetButton()
                                }
                                else -> {
                                    // Payment failed for some other reason
                                    Log.e("JUSPAY_DEBUG", "Payment FAILED with status: $status")
                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    resetButton()
                                }
                            }
                        }

                        // Hide any loading state
                        "hide_loader" -> {
                            Log.d("JUSPAY_DEBUG", "Hide loader event received")
                            resetButton()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("JUSPAY_DEBUG", "Callback Exception: ${e.message}")
                }
            }

            override fun onStartWaitingDialogCreated(view: View?) {}

            override fun getMerchantView(
                container: ViewGroup?,
                merchantViewType: MerchantViewType?
            ): View? = null

            override fun createJuspaySafeWebViewClient(): WebViewClient? = null
        }
    }

    // Re-enable the proceed button
    private fun resetButton() {
        binding.btnProceed.isEnabled = true
        binding.btnProceed.text = "Proceed Payment"
    }

    private fun setupUI() {
        binding.patientName.text = args.patientName
        val formattedDate = formatDate(args.bookingDate)
        val formattedSlot = formatSlot(args.timeSlot)
        binding.pickupDateTime.text = "$formattedDate, $formattedSlot"
    }

    private fun observeCartItems() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->

            // Skip if user came from direct test selection
            if (args.directTestId != null) return@observe

            binding.llTestsListContainer.removeAllViews()

            var subtotal = 0

            items?.forEach { test ->
                val row = layoutInflater.inflate(
                    R.layout.item_payment_test_row,
                    binding.llTestsListContainer,
                    false
                )

                row.findViewById<TextView>(R.id.tvTestName).text = test.name

                val finalPrice = test.displayPrice ?: test.price ?: 0

                row.findViewById<TextView>(R.id.tvTestPrice).text = "Rs. $finalPrice"

                binding.llTestsListContainer.addView(row)

                subtotal += finalPrice
            }

            // Store test IDs for booking reference
            testIdsJson = JSONArray(
                items?.map { it.id } ?: emptyList<String>()
            ).toString()

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