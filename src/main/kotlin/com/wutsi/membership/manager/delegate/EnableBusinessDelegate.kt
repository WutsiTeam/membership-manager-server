package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.dto.EnableBusinessRequest
import com.wutsi.membership.manager.workflow.EnableBusinessWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class EnableBusinessDelegate(
    private val workflow: EnableBusinessWorkflow,
    private val logger: KVLogger
) {
    public fun invoke(request: EnableBusinessRequest) {
        logger.add("request_display_name", request.displayName)
        logger.add("request_city_id", request.cityId)
        logger.add("request_biography", request.biography)
        logger.add("request_whatsapp", request.whatsapp)
        logger.add("request_category_id", request.categoryId)
        workflow.execute(
            WorkflowContext(request = request)
        )
    }
}
