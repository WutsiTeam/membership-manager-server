package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.access.enums.AccountStatus
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldNotBeSuspendedRule
import org.springframework.stereotype.Service

@Service
class DeleteMemberWorkflow(eventStream: EventStream) : AbstractMembershipWorkflow(eventStream) {
    override fun getEventType() = EventURN.MEMBER_DELETED.urn

    override fun toEventPayload(context: WorkflowContext) = MemberEventPayload(
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
