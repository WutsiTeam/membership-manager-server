package com.wutsi.membership.manager.workflow

import com.wutsi.event.BusinessEventPayload
import com.wutsi.event.EventURN
import com.wutsi.membership.manager.dto.ActivateBusinessRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.RegulationEngine
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldNotBeBusinessRule
import com.wutsi.workflow.rule.account.CountryShouldSupportBusinessAccountRule
import org.springframework.stereotype.Service

@Service
class ActivateBusinessWorkflow(
    private val regulationEngine: RegulationEngine,
    eventStream: EventStream,
) : AbstractBusinessWorkflow<ActivateBusinessRequest, Unit>(eventStream) {
    override fun getEventType(request: ActivateBusinessRequest, response: Unit, context: WorkflowContext) =
        EventURN.BUSINESS_ACTIVATED.urn

    override fun toEventPayload(request: ActivateBusinessRequest, response: Unit, context: WorkflowContext) =
        BusinessEventPayload(
            accountId = SecurityUtil.getAccountId(),
        )

    override fun getValidationRules(request: ActivateBusinessRequest, context: WorkflowContext): RuleSet {
        val account = getCurrentAccount(context)
        return RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldNotBeBusinessRule(account),
                CountryShouldSupportBusinessAccountRule(account, regulationEngine),
            ),
        )
    }

    override fun doExecute(request: ActivateBusinessRequest, context: WorkflowContext) {
        val account = getCurrentAccount(context)
        membershipAccessApi.enableBusiness(
            id = SecurityUtil.getAccountId(),
            request = com.wutsi.membership.access.dto.EnableBusinessRequest(
                displayName = request.displayName,
                country = account.country,
                cityId = request.cityId,
                categoryId = request.categoryId,
                biography = request.biography,
                whatsapp = request.whatsapp,
                email = request.email,
                name = request.name,
            ),
        )
    }
}
