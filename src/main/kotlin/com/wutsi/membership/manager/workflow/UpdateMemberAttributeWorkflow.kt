package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow(eventStream: EventStream) : AbstractMembershipWorkflow(eventStream) {
    override fun getEventType() = EventURN.MEMBER_ATTRIBUTE_UPDATED.urn

    override fun toEventPayload(context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(context: WorkflowContext): RuleSet {
        val account = getCurrentAccount()
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account)
            )
        )
    }

    override fun doExecute(context: WorkflowContext) {
        val request = context.request as UpdateMemberAttributeRequest
        membershipAccess.updateAccountAttribute(
            id = SecurityUtil.getAccountId(),
            request = UpdateAccountAttributeRequest(
                name = request.name,
                value = request.value
            )
        )
    }
}
