package com.example.etiotest.ui.theme.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.model.OrderItem
import com.example.etiotest.data.model.PaymentResponse
import com.example.etiotest.data.request.InitiatePaymentRequest
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.FragmentPaymentDetailsBinding
import com.google.gson.Gson
import `in`.juspay.hypersdk.core.MerchantViewType
import `in`.juspay.services.HyperServices
import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import org.json.JSONObject

class PaymentDetailFragment : Fragment() {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentOrderItem: OrderItem

    private lateinit var hyperServices: HyperServices

    private val viewModel: UserViewModel by viewModels {

        val api = RetrofitClient.getApiService(requireContext())

        UserViewModel.Factory(
            AuthRepository(api)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPaymentDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        hyperServices = HyperServices(requireActivity())

        initClicks()

        observeOrderDetails()

        observePaymentResponse()

        val orderId = arguments?.getString("order_id")

        if (orderId != null) {

            viewModel.getOrderDetailsById(orderId)

        } else {

            Toast.makeText(
                requireContext(),
                "Order ID not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initClicks() {

        binding.ivBack.setOnClickListener {

            findNavController().navigateUp()
        }

        binding.btnProceed.setOnClickListener {

            startPayment()
        }
    }

    private fun observeOrderDetails() {

        viewModel.orderDetail.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {

                    binding.btnProceed.isEnabled = false
                }

                is Resource.Success -> {

                    binding.btnProceed.isEnabled = true

                    res.data?.let {

                        currentOrderItem = it

                        populateUI(it)
                    }
                }

                is Resource.Error -> {

                    binding.btnProceed.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        res.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun populateUI(item: OrderItem) {

        binding.patientName.text = item.patient.name

        val dateOnly = item.bookingDate.substring(0, 10)

        binding.pickupDateTime.text =
            "$dateOnly, ${item.timeSlot.slotLabel}"

        binding.totalFare.text =
            "Rs. ${item.pricing.total}"

        setupTests(item)

        setupPriceBreakdown(item)
    }

    private fun setupTests(item: OrderItem) {

        binding.llTestsListContainer.removeAllViews()

        item.tests.forEach { test ->

            val tv = TextView(requireContext()).apply {

                text = "• ${test.name}"

                textSize = 15f

                setTextColor(Color.BLACK)

                setPadding(0, 8, 0, 8)

                typeface = ResourcesCompat.getFont(
                    requireContext(),
                    R.font.sf_medium
                )
            }

            binding.llTestsListContainer.addView(tv)
        }
    }

    private fun setupPriceBreakdown(item: OrderItem) {

        binding.llPriceBreakdownContainer.removeAllViews()

        item.tests.forEach { test ->

            val row = RelativeLayout(requireContext()).apply {

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                setPadding(0, 12, 0, 12)
            }

            val tvName = TextView(requireContext()).apply {

                text = "• ${test.name}"

                setTextColor(Color.BLACK)

                textSize = 14f

                typeface = ResourcesCompat.getFont(
                    requireContext(),
                    R.font.sf_medium
                )
            }

            val tvPrice = TextView(requireContext()).apply {

                text = "Rs. ${test.price}"

                setTextColor(Color.BLACK)

                textSize = 14f

                typeface = ResourcesCompat.getFont(
                    requireContext(),
                    R.font.sf_bold
                )

                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

                params.addRule(RelativeLayout.ALIGN_PARENT_END)

                layoutParams = params
            }

            row.addView(tvName)

            row.addView(tvPrice)

            binding.llPriceBreakdownContainer.addView(row)
        }

        addExtraChargeRow(
            "Platform Fee",
            "0"
        )

        addExtraChargeRow(
            "GST",
            "0"
        )
    }

    private fun addExtraChargeRow(
        title: String,
        amount: String
    ) {

        val row = RelativeLayout(requireContext()).apply {

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            setPadding(0, 12, 0, 12)
        }

        val tvName = TextView(requireContext()).apply {

            text = title

            setTextColor(Color.BLACK)

            textSize = 14f

            typeface = ResourcesCompat.getFont(
                requireContext(),
                R.font.sf_medium
            )
        }

        val tvPrice = TextView(requireContext()).apply {

            text = "Rs. $amount"

            setTextColor(Color.BLACK)

            textSize = 14f

            typeface = ResourcesCompat.getFont(
                requireContext(),
                R.font.sf_bold
            )

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            params.addRule(RelativeLayout.ALIGN_PARENT_END)

            layoutParams = params
        }

        row.addView(tvName)

        row.addView(tvPrice)

        binding.llPriceBreakdownContainer.addView(row)
    }

    private fun startPayment() {

        if (!::currentOrderItem.isInitialized) {

            Toast.makeText(
                requireContext(),
                "Order details missing",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        binding.btnProceed.isEnabled = false

        binding.btnProceed.text = "Processing..."

        val randomOrderId =
            (Math.random() * Math.pow(10.0, 12.0)).toLong()

        val request = InitiatePaymentRequest(

            order_id = "ORDER_$randomOrderId",

            amount = currentOrderItem.pricing.total.toString(),

            customer_id = currentOrderItem.patient.name,

            customer_email = "mightyshubham9055@gmail.com",

            customer_phone = currentOrderItem.patient.phone
        )

        viewModel.initiatePayment(request)
    }

    private fun observePaymentResponse() {

        viewModel.paymentResponse.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {

                    binding.btnProceed.isEnabled = false

                    binding.btnProceed.text = "Please wait..."
                }

                is Resource.Success -> {

                    binding.btnProceed.isEnabled = true

                    binding.btnProceed.text = "Proceed Payment"

                    res.data?.let {

                        openJuspaySDK(it)
                    }
                }

                is Resource.Error -> {

                    binding.btnProceed.isEnabled = true

                    binding.btnProceed.text = "Proceed Payment"

                    Toast.makeText(
                        requireContext(),
                        res.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun openJuspaySDK(
        response: PaymentResponse
    ) {

        try {

            val sdkPayload = JSONObject(
                Gson().toJson(response.sdkPayload)
            )
//            createHyperPaymentCallback()//
//            hyperServices.setCallback(
//                createHyperPaymentCallback()
//            )

            hyperServices.process(
                requireActivity(),
                sdkPayload
            )

        } catch (e: Exception) {

            Toast.makeText(
                requireContext(),
                e.message,
                Toast.LENGTH_LONG
            ).show()

            Log.e(
                "PAYMENT_ERROR",
                e.message.toString()
            )
        }
    }

    private fun createHyperPaymentCallback():
            HyperPaymentsCallbackAdapter {

        return object : HyperPaymentsCallbackAdapter() {

            override fun onEvent(
                jsonObject: JSONObject,
                responseHandler: JuspayResponseHandler
            ) {

                try {

                    Log.d(
                        "JUSPAY_RESPONSE",
                        jsonObject.toString()
                    )

                    val event =
                        jsonObject.optString("event")

                    when (event) {

                        "hide_loader" -> {

                            binding.btnProceed.isEnabled = true
                        }

                        "process_result" -> {

                            val payload =
                                jsonObject.optJSONObject("payload")

                            val status =
                                payload?.optString("status")

                            when (status) {

                                "charged" -> {

                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                "backpressed",
                                "user_aborted" -> {

                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Cancelled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {

                                    Toast.makeText(
                                        requireContext(),
                                        "Payment Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                } catch (e: Exception) {

                    Log.e(
                        "PAYMENT_CALLBACK",
                        e.message.toString()
                    )
                }
            }

            override fun onStartWaitingDialogCreated(
                view: View?
            ) {

            }

            override fun getMerchantView(
                container: ViewGroup?,
                merchantViewType: MerchantViewType?
            ): View? {

                return null
            }

            override fun createJuspaySafeWebViewClient():
                    WebViewClient? {

                return null
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}