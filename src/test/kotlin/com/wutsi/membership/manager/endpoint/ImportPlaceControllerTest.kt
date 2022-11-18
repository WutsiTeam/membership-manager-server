package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.membership.access.dto.SavePlaceRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImportPlaceControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun CM() {
        // GIVEN
        val response = rest.getForEntity(url("CM"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(10000)
        val request = argumentCaptor<SavePlaceRequest>()
        verify(membershipAccess, times(122)).savePlace(request.capture())
        verify(eventStream, never()).publish(any(), any())
    }

    fun url(country: String) = "http://localhost:$port/v1/places/import?country=$country"
}
