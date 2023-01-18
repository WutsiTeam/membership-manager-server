package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.event.BusinessEventPayload
import com.wutsi.event.EventURN
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeactivateBusinessControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun disable() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, businessId = 7777L)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(membershipAccess).disableBusiness(account.id)

        verify(eventStream).publish(
            EventURN.BUSINESS_DEACTIVATED.urn,
            BusinessEventPayload(accountId = account.id, businessId = account.businessId!!),
        )
    }

    @Test
    fun noBusinessId() {
        // GIVEN
        val account = Fixtures.createAccount(businessId = null)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(membershipAccess, never()).updateAccountAttribute(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    private fun url() = "http://localhost:$port/v1/members/business"
}
