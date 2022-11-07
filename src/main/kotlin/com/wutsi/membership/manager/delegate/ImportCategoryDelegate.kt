package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ImportCategoryDelegate(private val workflow: ImportCategoryWorkflow) {
    @Async
    fun invoke(language: String) {
        workflow.execute(
            context = WorkflowContext(
                request = mapOf(
                    ImportCategoryWorkflow.REQUEST_LANGUAGE to language
                )
            )
        )
    }
}
