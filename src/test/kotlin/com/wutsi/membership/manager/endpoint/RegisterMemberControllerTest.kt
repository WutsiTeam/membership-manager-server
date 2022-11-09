package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.RegisterMemberRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterMemberControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private val request = RegisterMemberRequest(
        phoneNumber = "+237670000010",
        displayName = "Ray Sponsible",
        pin = "123456",
        cityId = 111L,
        country = "CM"
    )

    @Test
    fun register() {
        // GIVEN
        val accountId = 111L
        doReturn(CreateAccountResponse(accountId)).whenever(membershipAccess).createAccount(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<CreateAccountRequest>()
        verify(membershipAccess).createAccount(req.capture())
        assertEquals(request.phoneNumber, req.firstValue.phoneNumber)
        assertEquals(request.displayName, req.firstValue.displayName)
        assertEquals(request.cityId, req.firstValue.cityId)
        assertEquals("CM", req.firstValue.country)
        assertEquals(language, req.firstValue.language)

        verify(eventStream).publish(
            EventURN.MEMBER_REGISTERED.urn,
            MemberEventPayload(
                accountId = accountId,
                phoneNumber = request.phoneNumber,
                pin = request.pin
            )
        )
    }

    @Test
    fun testAlreadyAssigned() {
        // GIVEN
        val e = createFeignNotFoundException(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn)
        doThrow(e).whenever(membershipAccess).createAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.workflow.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/members"
}
