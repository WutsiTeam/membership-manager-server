package com.wutsi.membership.manager.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.platform.core.stream.EventStream
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ImportCategoryJobTest {
    @MockBean
    protected lateinit var membershipAccess: MembershipAccessApi

    @MockBean
    protected lateinit var eventStream: EventStream

    @Autowired
    private lateinit var job: ImportCategoryJob

    @Test
    fun run() {
        // WHEN
        val response = job.runEN()

        // THEN
        assertEquals(0, response.errors.size)
        assertEquals(1556, response.imported)

        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())

        assertEquals("Abortion Service", request.firstValue.title)
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun runFR() {
        // WHEN
        val response = job.runFR()

        // THEN
        assertEquals(0, response.errors.size)
        assertEquals(1556, response.imported)

        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())

        assertEquals("Service d'avortement", request.firstValue.title)
        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun runWithError() {
        // GIVEN
        val ex = mock<FeignException>()
        doThrow(ex).whenever(membershipAccess).saveCategory(eq(10000), any())
        doThrow(ex).whenever(membershipAccess).saveCategory(eq(20000), any())

        // WHEN
        val response = job.runEN()

        // THEN
        assertEquals(2, response.errors.size)
        assertEquals(1554, response.imported)

        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(1556)).saveCategory(any(), request.capture())

        assertEquals("Abortion Service", request.firstValue.title)
        verify(eventStream, never()).publish(any(), any())
    }
}
