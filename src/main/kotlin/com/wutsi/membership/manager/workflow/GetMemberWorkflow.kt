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
class GetMemberWorkflow(
    private val objectMapper: ObjectMapper,
    eventStream: EventStream,
) : AbstractMembershipWorkflow<Long, GetMemberResponse>(eventStream) {
    override fun getEventType(
        memberId: Long,
        response: GetMemberResponse,
        context: WorkflowContext,
    ): String? = null

    override fun toEventPayload(
        memberId: Long,
        response: GetMemberResponse,
        context: WorkflowContext,
    ): MemberEventPayload? = null

    override fun getValidationRules(memberId: Long, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(memberId: Long, context: WorkflowContext): GetMemberResponse {
        try {
            val account = getAccount(memberId)
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
                        "account-id" to memberId,
                    ),
                ),
            )
        }
    }
}
