package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchMemberWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<SearchMemberRequest, SearchMemberResponse>(eventStream) {
    override fun getEventType(): String? = null

    override fun toEventPayload(
        request: SearchMemberRequest,
        response: SearchMemberResponse,
        context: WorkflowContext
    ): MemberEventPayload? = null

    override fun getValidationRules(request: SearchMemberRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: SearchMemberRequest, context: WorkflowContext): SearchMemberResponse {
        val accounts = membershipAccessApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = request.phoneNumber,
                status = AccountStatus.ACTIVE.name,
                limit = request.limit,
                offset = request.offset
            )
        ).accounts
        return SearchMemberResponse(
            members = accounts.map {
                MemberSummary(
                    id = it.id,
                    displayName = it.displayName,
                    pictureUrl = it.pictureUrl,
                    categoryId = it.categoryId,
                    business = it.business,
                    country = it.country,
                    cityId = it.cityId,
                    language = it.language,
                    active = it.status == AccountStatus.ACTIVE.name,
                    superUser = it.superUser
                )
            }
        )
    }
}
