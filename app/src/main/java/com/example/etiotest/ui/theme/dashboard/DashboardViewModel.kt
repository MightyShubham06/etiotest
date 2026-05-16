import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.etiotest.data.test.CartItem
import com.example.etiotest.data.test.Test

// ui/main/dashboard/DashboardViewModel.kt
class DashboardViewModel : ViewModel() {

    // LiveData for list of available tests
    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    // Cart contents
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: MutableLiveData<MutableList<CartItem>> = _cartItems

    init {
        loadTests()
    }

    private fun loadTests() {
        // load data e.g. from network or hardcoded for now
        val sample = listOf(
            Test("t1", "Blood Test", 250.0),
            Test("t2", "X‑Ray", 500.0),
            Test("t3", "MRI Scan", 5000.0),
            Test("t4", "Ultrasound", 1500.0)
        )
        _tests.value = sample
    }

    fun addToCart(test: Test) {
        val current = _cartItems.value ?: mutableListOf()
        val existing = current.find { it.test.id == test.id }
        if (existing != null) {
            existing.quantity += 1
        } else {
            current.add(CartItem(test, quantity = 1))
        }
        // Trigger LiveData change
        _cartItems.value = current
    }

    fun removeFromCart(test: Test) {
        val current = _cartItems.value ?: mutableListOf()
        current.removeAll { it.test.id == test.id }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}
