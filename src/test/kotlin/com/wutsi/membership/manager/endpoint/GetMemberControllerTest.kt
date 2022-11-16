package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetMemberControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        // GIVEN
        val account = Fixtures.createAccount(business = true, storeId = 1L, businessId = 22L)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val response = rest.getForEntity(url(), GetMemberResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val member = response.body!!.member
        assertEquals(account.id, member.id)
        assertEquals(account.displayName, member.displayName)
        assertEquals(account.email, member.email)
        assertEquals(account.country, member.country)
        assertEquals(account.business, member.business)
        assertEquals(account.storeId, member.storeId)
        assertEquals(account.businessId, member.businessId)
        assertEquals(account.category?.id, member.category?.id)
        assertEquals(account.category?.title, member.category?.title)
        assertEquals(account.city?.id, member.city?.id)
        assertEquals(account.city?.name, member.city?.name)
    }

    private fun url() = "http://localhost:$port/v1/members"
}
