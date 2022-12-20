package com.wutsi.membership.manager.workflow

import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SaveDeviceWorkflow(
    eventStream: EventStream,
) : AbstractMembershipWorkflow<SaveDeviceRequest, Unit>(eventStream) {
    override fun getEventType(
        request: SaveDeviceRequest,
        response: Unit,
        context: WorkflowContext,
    ): String? = null

    override fun toEventPayload(
        request: SaveDeviceRequest,
        response: Unit,
        context: WorkflowContext,
    ): MemberEventPayload? = null

    override fun getValidationRules(request: SaveDeviceRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: SaveDeviceRequest, context: WorkflowContext) {
        membershipAccessApi.saveAccountDevice(
            id = getCurrentAccountId(context),
            request = SaveAccountDeviceRequest(
                token = request.token,
                type = request.type,
                osVersion = request.osVersion,
                osName = request.osName,
                model = request.model,
            ),
        )
    }
}
