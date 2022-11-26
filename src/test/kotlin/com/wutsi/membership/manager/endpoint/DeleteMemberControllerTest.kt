package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.Fixtures
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.workflow.error.ErrorURN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteMemberControllerTest : AbstractSecuredControllerTest() {
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
                status = AccountStatus.SUSPENDED.name
            )
        )

        verify(eventStream).publish(
            EventURN.MEMBER_DELETED.urn,
            MemberEventPayload(accountId = ACCOUNT_ID)
        )
    }

    @Test
    fun alreadySuspended() {
        // GIVEN
        val account = Fixtures.createAccount(status = AccountStatus.SUSPENDED)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.delete(url())
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.MEMBER_SUSPENDED.urn, response.error.code)

        verify(membershipAccess, never()).updateAccountStatus(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    private fun url() = "http://localhost:$port/v1/members"
}
