package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.RuleSet
import com.wutsi.membership.manager.rule.impl.AccountShouldNotBeRegistered
import org.springframework.stereotype.Service

@Service
class StartMemberRegistrationWorkflow : AbstractWorkflow() {
    companion object {
        const val REQUEST_PHONE_NUMBER = "phone-number"
    }

    override fun getEventURN() = EventURN.MEMBER_REGISTRATION_STARTED

    override fun toMemberEventPayload(context: WorkflowContext) = MemberEventPayload(
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
