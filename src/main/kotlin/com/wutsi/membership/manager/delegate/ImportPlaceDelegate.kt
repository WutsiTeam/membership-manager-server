package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.ImportPlaceWorkflow
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ImportPlaceDelegate(
    private val workflow: ImportPlaceWorkflow
) {
    @Async
    fun invoke(country: String) {
        val logger = DefaultKVLogger()
        try {
            val response = workflow.execute(country, WorkflowContext())

            logger.add("import_count", response.imported)
            logger.add("error_count", response.errors.size)
        } finally {
            logger.log()
        }
    }
}
