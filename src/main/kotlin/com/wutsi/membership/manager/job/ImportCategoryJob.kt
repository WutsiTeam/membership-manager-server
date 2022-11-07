package com.wutsi.membership.manager.job

import com.wutsi.membership.manager.util.csv.CsvImportResponse
import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ImportCategoryJob(
    private val workflow: ImportCategoryWorkflow,
    lockManager: CronLockManager
) : AbstractCronJob(lockManager) {
    companion object {
        val LANGUAGES = listOf("en", "fr")
    }

    @Scheduled(cron = "\${wutsi.application.jobs.import-category.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        var result = 0L
        LANGUAGES.forEach {
            val response = run(it)
            result += (response?.imported ?: 0)
        }
        return result
    }

    private fun run(language: String): CsvImportResponse? {
        // Process
        val logger = DefaultKVLogger()
        try {
            val context = WorkflowContext(
                request = mapOf(ImportCategoryWorkflow.REQUEST_LANGUAGE to language)
            )
            workflow.execute(context)

            val response = context.response as CsvImportResponse?
            logger.add("imported", response?.imported)
            logger.add("errors", response?.errors?.size)

            return response
        } finally {
            logger.log()
        }
    }
}
