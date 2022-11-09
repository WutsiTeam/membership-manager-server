package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.dto.EnableBusinessRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.CountryRegulations
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldNotBeBusinessRule
import com.wutsi.workflow.rule.account.CountrySupportsBusinessAccountRule
import org.springframework.stereotype.Service

@Service
class EnableBusinessWorkflow(
    private val countryRegulations: CountryRegulations,
    eventStream: EventStream
) : AbstractMembershipWorkflow(eventStream) {
    override fun getEventType() = EventURN.BUSINESS_ACCOUNT_ENABLED.urn

    override fun toEventPayload(context: WorkflowContext) = MemberEventPayload(
        accountId = SecurityUtil.getAccountId()
    )

    override fun getValidationRules(context: WorkflowContext): RuleSet {
        val account = getCurrentAccount()
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldNotBeBusinessRule(account),
                CountrySupportsBusinessAccountRule(account, countryRegulations)
            )
        )
    }

    override fun doExecute(context: WorkflowContext) {
        val request = context.request as EnableBusinessRequest
        val account = getCurrentAccount()

        membershipAccess.enableBusiness(
            id = SecurityUtil.getAccountId(),
            request = com.wutsi.membership.access.dto.EnableBusinessRequest(
                displayName = request.displayName,
                country = account.country,
                cityId = request.cityId,
                categoryId = request.categoryId,
                biography = request.biography,
                whatsapp = request.whatsapp
            )
        )
    }
}
