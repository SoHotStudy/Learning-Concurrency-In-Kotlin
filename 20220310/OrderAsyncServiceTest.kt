import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class OrderAsyncServiceTest : WithAssertions {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var orderAsyncService: OrderAsyncService
    private lateinit var orderService: OrderService

    @BeforeEach
    internal fun setUp() {
        orderService = mockk()
        orderAsyncService = OrderAsyncService(orderService, testDispatcher)
    }

    @Test
    fun `주문에 성공한다`() = runTest(testDispatcher) {
        // given
        val productId = "product1"
        val memberId = "member1"
        coEvery { orderService.order(any(), any()) } returns Order(
            purchaser = Member(id = memberId, name = "purchaser1"),
            product = Product(id = productId, price = 1000.toBigDecimal())
        )

        // when
        orderAsyncService.order(productId, memberId)
        runCurrent()

        // then
        assertThat(orderAsyncService.order).usingRecursiveComparison()
            .isEqualTo(
                Order(
                    product = Product(productId, 1000.toBigDecimal()),
                    purchaser = Member(memberId, "purchaser1")
                )
            )
        coVerify { orderService.order(productId, memberId) }
        confirmVerified(orderService)
    }
}