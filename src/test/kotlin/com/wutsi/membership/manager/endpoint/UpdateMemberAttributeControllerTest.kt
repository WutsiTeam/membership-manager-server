package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateMemberAttributeControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    val request = UpdateMemberAttributeRequest(
        name = "display-name",
        value = "Yo Man",
    )

    @Test
    fun update() {
        // GIVEN
        val account = Fixtures.createAccount()
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipAccess).updateAccountAttribute(
            ACCOUNT_ID,
            UpdateAccountAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )

        verify(eventStream).publish(
            EventURN.MEMBER_ATTRIBUTE_UPDATED.urn,
            MemberEventPayload(accountId = ACCOUNT_ID),
        )
    }

    @Test
    fun memberNotActive() {
        // GIVEN
        val account = Fixtures.createAccount(status = AccountStatus.INACTIVE)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        verify(membershipAccess, never()).updateAccountAttribute(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    private fun url() = "http://localhost:$port/v1/members/attributes"
}
