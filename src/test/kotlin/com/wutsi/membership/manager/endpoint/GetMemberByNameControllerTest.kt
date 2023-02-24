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
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetMemberByNameControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun getMember() {
        // GIVEN
        val accountId = 111L
        val account = Fixtures.createAccount(
            id = accountId,
            business = true,
            storeId = 1L,
            businessId = 22L,
            name = "ray.sponsible",
        )
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccountByName(any())

        // WHEN
        val response = RestTemplate().getForEntity(url("ray.sponsible"), GetMemberResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val member = response.body!!.member
        assertEquals(account.id, member.id)
        assertEquals(account.name, member.name)
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

    private fun url(name: String) = "http://localhost:$port/v1/members/@$name"
}
