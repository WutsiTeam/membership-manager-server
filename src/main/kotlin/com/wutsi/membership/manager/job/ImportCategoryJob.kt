package com.wutsi.membership.manager.job

import com.wutsi.membership.manager.util.csv.CsvImportResponse
import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ImportCategoryJob(
    private val workflow: ImportCategoryWorkflow,
    @Value("\${wutsi.application.jobs.import-category.url}") private val url: String
) {
    @Scheduled(cron = "\${wutsi.application.jobs.import-category.cron-en}")
    fun runEN() = run("en")

    @Scheduled(cron = "\${wutsi.application.jobs.import-category.cron-fr}")
    fun runFR() = run("fr")

    private fun run(language: String): CsvImportResponse {
        val logger = DefaultKVLogger()
        try {
            val context = WorkflowContext(
                request = mapOf(
                    ImportCategoryWorkflow.REQUEST_LANGUAGE to language,
                    ImportCategoryWorkflow.REQUEST_URL to url
                )
            )
            workflow.execute(
                context = context
            )

            val response = context.response as CsvImportResponse
            logger.add("job", this::class.java.simpleName)
            logger.add("imported", response.imported)
            logger.add("errors", response.errors.size)

            return response
        } finally {
            logger.log()
        }
    }
}
