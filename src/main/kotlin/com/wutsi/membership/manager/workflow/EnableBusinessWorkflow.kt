package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.dto.EnableBusinessRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.rule.AccountShouldBeActiveRule
import com.wutsi.membership.manager.rule.AccountShouldNotBeBusinessRule
import com.wutsi.membership.manager.rule.CountrySupportsBusinessAccountRule
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.regulation.CountryRegulations
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class EnableBusinessWorkflow(
    private val countryRegulations: CountryRegulations
) : AbstractWorkflow() {
    override fun getEventType() = EventURN.BUSINESS_ACCOUNT_ENABLED.urn

    override fun toMemberEventPayload(context: WorkflowContext) = MemberEventPayload(
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
