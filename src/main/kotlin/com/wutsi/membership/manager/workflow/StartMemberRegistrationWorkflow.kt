package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldNotBeRegistered
import org.springframework.stereotype.Service

@Service
class StartMemberRegistrationWorkflow(eventStream: EventStream) : AbstractMembershipWorkflow(eventStream) {
    companion object {
        const val REQUEST_PHONE_NUMBER = "phone-number"
    }

    override fun getEventType() = EventURN.MEMBER_REGISTRATION_STARTED.urn

    override fun toEventPayload(context: WorkflowContext) = MemberEventPayload(
        phoneNumber = getPhoneNumber(context)
    )

    override fun getValidationRules(context: WorkflowContext): RuleSet {
        return RuleSet(
            listOf(
                AccountShouldNotBeRegistered(getPhoneNumber(context), membershipAccess)
            )
        )
    }

    override fun doExecute(context: WorkflowContext) {
    }

    private fun getPhoneNumber(context: WorkflowContext): String =
        (context.request as Map<String, String>)[REQUEST_PHONE_NUMBER]!!
}
