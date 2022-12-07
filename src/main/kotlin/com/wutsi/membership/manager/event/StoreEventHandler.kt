package com.wutsi.membership.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.StoreEventPayload
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.workflow.UpdateMemberAttributeWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class StoreEventHandler(
    private val updateMemberAttributeWorkflow: UpdateMemberAttributeWorkflow,
    private val mapper: ObjectMapper,
    private val logger: KVLogger
) {
    fun onStoreActivated(event: Event) {
        val payload = toStorePayload(event)
        log(payload)

        updateMemberAttributeWorkflow.execute(
            request = UpdateMemberAttributeRequest(
                name = "store-id",
                value = payload.storeId.toString()
            ),
            context = WorkflowContext(
                accountId = payload.accountId
            )
        )
    }

    fun onStoreDeactivated(event: Event) {
        val payload = toStorePayload(event)
        log(payload)

        updateMemberAttributeWorkflow.execute(
            request = UpdateMemberAttributeRequest(
                name = "store-id",
                value = null
            ),
            context = WorkflowContext(
                accountId = payload.accountId
            )
        )
    }

    private fun toStorePayload(event: Event): StoreEventPayload =
        mapper.readValue(event.payload, StoreEventPayload::class.java)

    private fun log(payload: StoreEventPayload) {
        logger.add("payload_account_id", payload.accountId)
        logger.add("payload_store_id", payload.storeId)
    }
}
