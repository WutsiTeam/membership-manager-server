package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeactivateMemberControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun suspend() {
        // GIVEN
        val account = Fixtures.createAccount()
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(membershipAccess).updateAccountStatus(
            ACCOUNT_ID,
            UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name
            )
        )

        verify(eventStream).publish(
            EventURN.MEMBER_DELETED.urn,
            MemberEventPayload(accountId = ACCOUNT_ID)
        )
    }

    private fun url() = "http://localhost:$port/v1/members"
}
