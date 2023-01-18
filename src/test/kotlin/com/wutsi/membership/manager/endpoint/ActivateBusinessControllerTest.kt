package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.error.ErrorURN
import com.wutsi.event.BusinessEventPayload
import com.wutsi.event.EventURN
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.ActivateBusinessRequest
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActivateBusinessControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private val request = ActivateBusinessRequest(
        cityId = 99999L,
        displayName = "Yo Man",
        whatsapp = true,
        biography = "This is a description",
        categoryId = 1213232L,
        email = "info@fake-news.com",
    )

    @Test
    fun enable() {
        // GIVEN
        val account = Fixtures.createAccount()
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        rest.postForEntity(url(), request, Any::class.java)

        // THEN
        val req = argumentCaptor<com.wutsi.membership.access.dto.EnableBusinessRequest>()
        verify(membershipAccess).enableBusiness(eq(ACCOUNT_ID), req.capture())

        assertEquals(account.country, req.firstValue.country)
        assertEquals(request.categoryId, req.firstValue.categoryId)
        assertEquals(request.cityId, req.firstValue.cityId)
        assertEquals(request.whatsapp, req.firstValue.whatsapp)
        assertEquals(request.displayName, req.firstValue.displayName)
        assertEquals(request.biography, req.firstValue.biography)
        assertEquals(request.email, req.firstValue.email)

        verify(eventStream).publish(
            EventURN.BUSINESS_ACTIVATED.urn,
            BusinessEventPayload(accountId = ACCOUNT_ID),
        )
    }

    @Test
    fun alreadyBusiness() {
        // GIVEN
        val account = Fixtures.createAccount(business = true)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.MEMBER_ALREADY_BUSINESS.urn, response.error.code)

        verify(membershipAccess, never()).updateAccountAttribute(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun notActive() {
        // GIVEN
        val account = Fixtures.createAccount(status = AccountStatus.INACTIVE)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.MEMBER_NOT_ACTIVE.urn, response.error.code)

        verify(membershipAccess, never()).updateAccountAttribute(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun countryNotSupported() {
        // GIVEN
        val account = Fixtures.createAccount(country = "NZ")
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.BUSINESS_ACCOUNT_NOT_SUPPORTED_IN_COUNTRY.urn, response.error.code)

        verify(membershipAccess, never()).updateAccountAttribute(any(), any())
        verify(eventStream, never()).publish(any(), any())
    }

    private fun url() = "http://localhost:$port/v1/members/business"
}
