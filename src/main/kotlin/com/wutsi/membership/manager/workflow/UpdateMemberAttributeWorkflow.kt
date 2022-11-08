package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.AccountShouldBeActiveRule
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow : AbstractWorkflow() {
    override fun getEventType() = EventURN.MEMBER_ATTRIBUTE_UPDATED.urn

    override fun toMemberEventPayload(context: WorkflowContext) = MemberEventPayload(
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