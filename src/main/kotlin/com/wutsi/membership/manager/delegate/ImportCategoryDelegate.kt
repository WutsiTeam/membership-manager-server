package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ImportCategoryDelegate(
    private val workflow: ImportCategoryWorkflow
) {
    @Async
    fun invoke(language: String) {
        val logger = DefaultKVLogger()
        try {
            val response = workflow.execute(language, WorkflowContext())

            logger.add("import_count", response.imported)
            logger.add("error_count", response.errors.size)
        } finally {
            logger.log()
        }
    }
}
