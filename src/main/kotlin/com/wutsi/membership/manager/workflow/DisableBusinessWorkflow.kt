package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldBeBusinessRule
import org.springframework.stereotype.Service

@Service
class DisableBusinessWorkflow(
    eventStream: EventStream
) : AbstractMembershipWorkflow(eventStream) {
    override fun getEventType() = EventURN.BUSINESS_ACCOUNT_DISABLED.urn

    override fun toEventPayload(context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(context: WorkflowContext): RuleSet {
        val account = getCurrentAccount()
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account)
            )
        )
    }

    override fun doExecute(context: WorkflowContext) {
        membershipAccess.disableBusiness(
            id = SecurityUtil.getAccountId()
        )
    }
}
