package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeactivateMemberWorkflow(eventStream: EventStream) : AbstractMembershipWorkflow<Void?, Unit>(eventStream) {
    override fun getEventType(request: Void?, response: Unit, context: WorkflowContext) = EventURN.MEMBER_DELETED.urn

    override fun toEventPayload(request: Void?, response: Unit, context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(request: Void?, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: Void?, context: WorkflowContext) {
        membershipAccessApi.updateAccountStatus(
            id = SecurityUtil.getAccountId(),
            request = UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name
            )
        )
    }
}
