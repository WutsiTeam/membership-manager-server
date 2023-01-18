package com.wutsi.membership.manager.workflow

import com.wutsi.event.BusinessEventPayload
import com.wutsi.event.EventURN
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeactivateBusinessWorkflow(
    eventStream: EventStream,
) : AbstractBusinessWorkflow<Void?, Long?>(eventStream) {
    override fun getEventType(request: Void?, businessId: Long?, context: WorkflowContext) =
        EventURN.BUSINESS_DEACTIVATED.urn

    override fun toEventPayload(request: Void?, businessId: Long?, context: WorkflowContext) = businessId?.let {
        BusinessEventPayload(
            accountId = SecurityUtil.getAccountId(),
            businessId = it,
        )
    }

    override fun getValidationRules(request: Void?, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: Void?, context: WorkflowContext): Long? {
        val account = getCurrentAccount(context)
        if (account.businessId != null) {
            membershipAccessApi.disableBusiness(
                id = account.id,
            )
        }
        return account.businessId
    }
}
