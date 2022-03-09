import kotlinx.coroutines.delay

data class Member(
    val id: String,
    val name: String,
)

class MemberService {
    suspend fun findMemberById(memberId: String): Member {
        delay(1000)
        return Member(memberId, "purchaser1")
    }
}