package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow(
    private val mapper: ObjectMapper,
    eventStream: EventStream,
) : AbstractMembershipWorkflow<UpdateMemberAttributeRequest, Unit>(eventStream) {
    override fun getEventType(request: UpdateMemberAttributeRequest, response: Unit, context: WorkflowContext) =
        EventURN.MEMBER_ATTRIBUTE_UPDATED.urn

    override fun toEventPayload(request: UpdateMemberAttributeRequest, response: Unit, context: WorkflowContext) =
        MemberEventPayload(
            accountId = getCurrentAccountId(context),
        )

    override fun getValidationRules(request: UpdateMemberAttributeRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: UpdateMemberAttributeRequest, context: WorkflowContext) {
        try {
            membershipAccessApi.updateAccountAttribute(
                id = getCurrentAccountId(context),
                request = UpdateAccountAttributeRequest(
                    name = request.name,
                    value = request.value,
                ),
            )
        } catch (ex: FeignException) {
            val errorResponse = toErrorResponse(ex)
            if (errorResponse?.error?.code == ErrorURN.NAME_ALREADY_ASSIGNED.urn) {
                throw ConflictException(
                    error = Error(
                        code = com.wutsi.error.ErrorURN.USERNAME_ALREADY_ASSIGNED.urn,
                        data = mapOf(
                            "name" to (request.value ?: ""),
                        ),
                    ),
                )
            } else {
                throw ex
            }
        }
    }

    private fun toErrorResponse(ex: FeignException): ErrorResponse? =
        try {
            mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
}
