import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockKExtension::class)
internal class OrderServiceTest : WithAssertions {
    @InjectMockKs
    private lateinit var orderService: OrderService

    @MockK
    private lateinit var productService: ProductService

    @MockK
    private lateinit var memberService: MemberService

    @Test
    @Timeout(1000, unit = TimeUnit.MILLISECONDS)
    fun `지연이 없이 주문되는 경우`() = runTest {
        // given
        val productId = "product1"
        val memberId = "member1"
        coEvery { productService.findProductById(any()) } returns Product(productId, 10.toBigDecimal())
        coEvery { memberService.findMemberById(any()) } returns Member(memberId, "purchaser_1")

        // when
        val order = orderService.order(productId, memberId)

        // then
        assertThat(order).usingRecursiveComparison()
            .isEqualTo(
                Order(
                    product = Product(productId, 10.toBigDecimal()),
                    purchaser = Member(memberId, "purchaser_1")
                )
            )
        coVerify {
            productService.findProductById(productId)
            memberService.findMemberById(memberId)
        }
        confirmVerified(productService, memberService)
    }

    @Test
    @Timeout(1000, unit = TimeUnit.MILLISECONDS)
    fun `상품쪽에 지연되어 주문되는 경우`() = runTest {
        // given
        val productId = "product1"
        val memberId = "member1"
        coEvery { productService.findProductById(any()) } coAnswers {
            delay(1_000)
            Product(productId, 10.toBigDecimal())
        }
        coEvery { memberService.findMemberById(any()) } returns Member(memberId, "purchaser_1")

        // when
        val order = orderService.order(productId, memberId)

        // then
        assertThat(order).usingRecursiveComparison()
            .isEqualTo(
                Order(
                    product = Product(productId, 10.toBigDecimal()),
                    purchaser = Member(memberId, "purchaser_1")
                )
            )
        coVerify {
            productService.findProductById(productId)
            memberService.findMemberById(memberId)
        }
        confirmVerified(productService, memberService)
    }
}