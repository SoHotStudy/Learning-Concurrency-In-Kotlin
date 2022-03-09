import kotlinx.coroutines.delay

data class Order(
    val purchaser: Member,
    val product: Product
)

class OrderService(
    private val productService: ProductService = ProductService(),
    private val memberService: MemberService = MemberService(),
) {

    suspend fun order(productId: String, memberId: String): Order {
        val product = productService.findProductById(productId)
        val member = memberService.findMemberById(memberId)

        delay(1000)

        return Order(purchaser = member, product = product)
    }
}