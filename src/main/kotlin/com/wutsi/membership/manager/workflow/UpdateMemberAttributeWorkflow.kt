package com.wutsi.membership.manager.workflow

import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<UpdateMemberAttributeRequest, Unit>(eventStream) {
    override fun getEventType(request: UpdateMemberAttributeRequest, response: Unit, context: WorkflowContext) =
        EventURN.MEMBER_ATTRIBUTE_UPDATED.urn

    override fun toEventPayload(request: UpdateMemberAttributeRequest, response: Unit, context: WorkflowContext) =
        MemberEventPayload(
            accountId = getCurrentAccountId(context)
        )

    override fun getValidationRules(request: UpdateMemberAttributeRequest, context: WorkflowContext): RuleSet {
        val account = getCurrentAccount(context)
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account)
            )
        )
    }

    override fun doExecute(request: UpdateMemberAttributeRequest, context: WorkflowContext) {
        membershipAccessApi.updateAccountAttribute(
            id = getCurrentAccountId(context),
            request = UpdateAccountAttributeRequest(
                name = request.name,
                value = request.value
            )
        )
    }
}
