package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.SaveCategoryRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImportCategoryControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun en() {
        // GIVEN
        val response = rest.getForEntity(url("en"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(10000)
        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())

        assertEquals("Abortion Service", request.firstValue.title)
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun fr() {
        // WHEN
        val response = rest.getForEntity(url("fr"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(10000)
        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())

        assertEquals("Service d'avortement", request.firstValue.title)
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun error() {
        // GIVEN
        val ex = createFeignNotFoundException(code = "foo")
        doThrow(ex).whenever(membershipAccess).saveCategory(eq(10000), any())

        // WHEN
        val response = rest.getForEntity(url("fr"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(10000)
        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())
    }

    fun url(language: String) = "http://localhost:$port/v1/categories/import?language=$language"
}
