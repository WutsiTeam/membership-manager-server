package com.wutsi.membership.manager.job

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ImportCategoryJobTest {
    @MockBean
    protected lateinit var workflow: ImportCategoryWorkflow

    @Autowired
    private lateinit var job: ImportCategoryJob

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        val context = argumentCaptor<WorkflowContext>()
        verify(workflow, times(2)).execute(context.capture())

        assertEquals(
            mapOf(ImportCategoryWorkflow.REQUEST_LANGUAGE to "en"),
            context.firstValue.request
        )

        assertEquals(
            mapOf(ImportCategoryWorkflow.REQUEST_LANGUAGE to "fr"),
            context.secondValue.request
        )
    }

    private fun toContext(lang: String) = WorkflowContext(
        request = mapOf(
            ImportCategoryWorkflow.REQUEST_LANGUAGE to lang
        )
    )
}
