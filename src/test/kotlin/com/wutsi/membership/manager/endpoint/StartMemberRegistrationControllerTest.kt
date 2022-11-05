package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.SearchAccountResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.net.URLEncoder
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StartMemberRegistrationControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private val phoneNumber = "+237670000001"

    @Test
    fun newMember() {
        // GIVEN
        doReturn(SearchAccountResponse()).whenever(membershipAccess).searchAccount(any())

        // WHEN
        val response = rest.getForEntity(url(), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(eventStream).publish(
            EventURN.MEMBER_REGISTRATION_STARTED.urn,
            MemberEventPayload(
                phoneNumber = phoneNumber
            )
        )
    }

    @Test
    fun existingMember() {
        // GIVEN
        val account = Fixtures.createAccountSummary()
        doReturn(SearchAccountResponse(listOf(account))).whenever(membershipAccess).searchAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(), Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.MEMBER_ALREADY_REGISTERED.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/members/start-registration?phone-number=" +
        URLEncoder.encode(phoneNumber, "utf-8")
}
