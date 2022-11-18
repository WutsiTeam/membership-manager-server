package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.dto.PlaceSummary
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchPlaceWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<SearchPlaceRequest, SearchPlaceResponse>(eventStream) {
    override fun getEventType(): String? = null

    override fun toEventPayload(
        request: SearchPlaceRequest,
        response: SearchPlaceResponse,
        context: WorkflowContext
    ): MemberEventPayload? = null

    override fun getValidationRules(request: SearchPlaceRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: SearchPlaceRequest, context: WorkflowContext): SearchPlaceResponse {
        val places = membershipAccessApi.searchPlace(
            request = com.wutsi.membership.access.dto.SearchPlaceRequest(
                keyword = request.keyword,
                type = request.type,
                country = request.country,
                limit = request.limit,
                offset = request.offset
            )
        ).places
        return SearchPlaceResponse(
            places = places.map {
                PlaceSummary(
                    id = it.id,
                    type = it.type,
                    name = it.name,
                    longName = it.longName
                )
            }
        )
    }
}
