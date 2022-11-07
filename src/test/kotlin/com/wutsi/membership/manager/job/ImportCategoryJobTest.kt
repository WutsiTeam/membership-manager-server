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

    val totalCategories = 1556
    val totalLanguages = ImportCategoryJob.LANGUAGES.size

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(totalLanguages * totalCategories)).saveCategory(any(), request.capture())

        assertEquals("Abortion Service", request.firstValue.title)
        assertEquals("Service d'avortement", request.allValues[totalCategories].title)
        verify(eventStream, never()).publish(any(), any())
    }


    @Test
    fun runWithError() {
        // GIVEN
        val ex = mock<FeignException>()
        doThrow(ex).whenever(membershipAccess).saveCategory(eq(10000), any())
        doThrow(ex).whenever(membershipAccess).saveCategory(eq(20000), any())

        // WHEN
        job.run()

        // THEN
        val request = argumentCaptor<SaveCategoryRequest>()
        verify(membershipAccess, times(totalLanguages * totalCategories)).saveCategory(any(), request.capture())
    }
}
