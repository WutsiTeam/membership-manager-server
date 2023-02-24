package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.error.ErrorURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetMemberByNameWorkflow(
    private val objectMapper: ObjectMapper,
    eventStream: EventStream,
) : AbstractMembershipWorkflow<String, GetMemberResponse>(eventStream) {
    override fun getEventType(
        name: String,
        response: GetMemberResponse,
        context: WorkflowContext,
    ): String? = null

    override fun toEventPayload(
        name: String,
        response: GetMemberResponse,
        context: WorkflowContext,
    ): MemberEventPayload? = null

    override fun getValidationRules(name: String, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(name: String, context: WorkflowContext): GetMemberResponse {
        try {
            val account = membershipAccessApi.getAccountByName(name).account
            return GetMemberResponse(
                member = objectMapper.readValue(
                    objectMapper.writeValueAsString(account),
                    Member::class.java,
                ),
            )
        } catch (ex: NotFoundException) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_FOUND.urn,
                    data = mapOf(
                        "name" to name,
                    ),
                ),
            )
        }
    }
}
