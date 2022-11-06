package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.manager.dto.RegisterMemberRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.RuleSet
import com.wutsi.membership.manager.util.PhoneUtil
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class RegisterMemberWorkflow : AbstractWorkflow() {
    override fun getEventURN() = EventURN.MEMBER_REGISTERED

    override fun toMemberEventPayload(context: WorkflowContext): MemberEventPayload {
        val request = context.request as RegisterMemberRequest
        return MemberEventPayload(
            accountId = context.response as Long,
            phoneNumber = request.phoneNumber,
            pin = request.pin
        )
    }

    override fun getValidationRules(context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(context: WorkflowContext) {
        val request = context.request as RegisterMemberRequest
        context.response = membershipAccess.createAccount(
            request = CreateAccountRequest(
                phoneNumber = request.phoneNumber,
                displayName = request.displayName,
                country = PhoneUtil.detectCountry(request.phoneNumber),
                language = LocaleContextHolder.getLocale().language
            )
        ).accountId
    }
}
