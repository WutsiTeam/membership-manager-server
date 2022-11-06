package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.access.enums.AccountStatus
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.RuleSet
import com.wutsi.membership.manager.rule.impl.AccountShouldNotBeSuspendedRule
import com.wutsi.membership.manager.util.SecurityUtil
import org.springframework.stereotype.Service

@Service
class DeleteMemberWorkflow : AbstractWorkflow() {
    override fun getEventURN() = EventURN.MEMBER_DELETED

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
