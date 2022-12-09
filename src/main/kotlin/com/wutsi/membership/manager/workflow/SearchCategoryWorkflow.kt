package com.wutsi.membership.manager.workflow

import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.manager.dto.CategorySummary
import com.wutsi.membership.manager.dto.SearchCategoryRequest
import com.wutsi.membership.manager.dto.SearchCategoryResponse
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchCategoryWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<SearchCategoryRequest, SearchCategoryResponse>(eventStream) {
    override fun getEventType(
        request: SearchCategoryRequest,
        response: SearchCategoryResponse,
        context: WorkflowContext
    ): String? = null

    override fun toEventPayload(
        request: SearchCategoryRequest,
        response: SearchCategoryResponse,
        context: WorkflowContext
    ): MemberEventPayload? = null

    override fun getValidationRules(request: SearchCategoryRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: SearchCategoryRequest, context: WorkflowContext): SearchCategoryResponse {
        val categories = membershipAccessApi.searchCategory(
            request = com.wutsi.membership.access.dto.SearchCategoryRequest(
                keyword = request.keyword,
                categoryIds = request.categoryIds,
                limit = request.limit,
                offset = request.offset
            )
        ).categories
        return SearchCategoryResponse(
            categories = categories.map {
                CategorySummary(
                    id = it.id,
                    title = it.title
                )
            }
        )
    }
}
