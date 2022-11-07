package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.access.enums.AccountStatus
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.AccountShouldNotBeSuspendedRule
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeleteMemberWorkflow : AbstractWorkflow() {
    override fun getEventType() = EventURN.MEMBER_DELETED.urn

    override fun toMemberEventPayload(context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(context: WorkflowContext): RuleSet {
        val account = getCurrentAccount()
        return RuleSet(
            listOf(
                AccountShouldNotBeSuspendedRule(account)
            )
        )
    }

    override fun doExecute(context: WorkflowContext) {
        membershipAccess.updateAccountStatus(
            id = SecurityUtil.getAccountId(),
            request = UpdateAccountStatusRequest(
                status = AccountStatus.SUSPENDED.name
            )
        )
    }
}
