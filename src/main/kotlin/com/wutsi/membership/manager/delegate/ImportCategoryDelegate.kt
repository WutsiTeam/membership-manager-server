package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.util.csv.CsvImportResponse
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
        logger.add("request_language", language)
        try {
            val context = WorkflowContext(
                request = mapOf(
                    ImportCategoryWorkflow.REQUEST_LANGUAGE to language
                )
            )
            workflow.execute(context = context)
            val response = context.response as CsvImportResponse
            logger.add("import_count", response.imported)
            logger.add("error_count", response.errors.size)
        } finally {
            logger.log()
        }
    }
}
