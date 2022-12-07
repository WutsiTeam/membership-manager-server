package com.wutsi.membership.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.BusinessEventPayload
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.workflow.UpdateMemberAttributeWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class BusinessEventHandler(
    private val updateMemberAttributeWorkflow: UpdateMemberAttributeWorkflow,
    private val mapper: ObjectMapper,
    private val logger: KVLogger
) {
    fun onBusinessCreated(event: Event) {
        val payload = toBusinessEvent(event)
        log(payload)

        updateMemberAttributeWorkflow.execute(
            request = UpdateMemberAttributeRequest(
                name = "business-id",
                value = payload.businessId.toString()
            ),
            context = WorkflowContext(
                accountId = payload.accountId
            )
        )
    }

    private fun toBusinessEvent(event: Event): BusinessEventPayload =
        mapper.readValue(event.payload, BusinessEventPayload::class.java)

    private fun log(payload: BusinessEventPayload) {
        logger.add("payload_account_id", payload.accountId)
        logger.add("payload_business_id", payload.businessId)
    }
}
