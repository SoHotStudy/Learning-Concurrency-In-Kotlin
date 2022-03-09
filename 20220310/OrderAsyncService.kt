import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OrderAsyncService(
    private val orderService: OrderService = OrderService(),
    private val dispatcher: CoroutineDispatcher
) {
    lateinit var order: Order

    fun order(productId: String, memberId: String) {
        CoroutineScope(dispatcher).launch {
            order = orderService.order(productId, memberId)
        }
    }
}