package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.ActivateBusinessRequest
import com.wutsi.membership.manager.workflow.ActivateBusinessWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class ActivateBusinessDelegate(
    private val workflow: ActivateBusinessWorkflow,
    private val logger: KVLogger,
) {
    public fun invoke(request: ActivateBusinessRequest) {
        logger.add("request_display_name", request.displayName)
        logger.add("request_city_id", request.cityId)
        logger.add("request_biography", request.biography)
        logger.add("request_whatsapp", request.whatsapp)
        logger.add("request_category_id", request.categoryId)

        workflow.execute(request, WorkflowContext())
    }
}
