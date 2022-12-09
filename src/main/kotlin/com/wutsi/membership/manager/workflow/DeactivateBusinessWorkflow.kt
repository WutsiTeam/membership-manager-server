package com.wutsi.membership.manager.workflow

import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldBeBusinessRule
import org.springframework.stereotype.Service

@Service
class DeactivateBusinessWorkflow(
    eventStream: EventStream
) : AbstractMembershipWorkflow<Void?, Unit>(eventStream) {
    override fun getEventType(request: Void?, response: Unit, context: WorkflowContext) =
        EventURN.BUSINESS_DEACTIVATED.urn

    override fun toEventPayload(request: Void?, response: Unit, context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(request: Void?, context: WorkflowContext): RuleSet {
        val account = getCurrentAccount(context)
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account)
            )
        )
    }

    override fun doExecute(request: Void?, context: WorkflowContext) {
        membershipAccessApi.disableBusiness(
            id = SecurityUtil.getAccountId()
        )
    }
}
