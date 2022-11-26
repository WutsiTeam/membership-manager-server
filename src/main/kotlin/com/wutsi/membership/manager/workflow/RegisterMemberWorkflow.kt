package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.EventURN
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.RegisterMemberRequest
import com.wutsi.membership.manager.util.PhoneUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class RegisterMemberWorkflow(
    private val mapper: ObjectMapper,
    eventStream: EventStream
) : AbstractMembershipWorkflow<RegisterMemberRequest, Long>(eventStream) {
    override fun getEventType() = EventURN.MEMBER_REGISTERED.urn

    override fun toEventPayload(
        request: RegisterMemberRequest,
        accountId: Long,
        context: WorkflowContext
    ): MemberEventPayload {
        return MemberEventPayload(
            accountId = accountId,
            phoneNumber = request.phoneNumber,
            pin = request.pin
        )
    }

    override fun getValidationRules(request: RegisterMemberRequest, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: RegisterMemberRequest, context: WorkflowContext): Long {
        try {
            return membershipAccessApi.createAccount(
                request = CreateAccountRequest(
                    phoneNumber = request.phoneNumber,
                    displayName = request.displayName,
                    country = PhoneUtil.detectCountry(request.phoneNumber),
                    language = LocaleContextHolder.getLocale().language,
                    cityId = request.cityId
                )
            ).accountId
        } catch (ex: FeignException) {
            val errorResponse = toErrorResponse(ex)
            if (errorResponse?.error?.code == ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn) {
                throw ConflictException(
                    error = Error(
                        code = com.wutsi.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                        data = mapOf(
                            "phone-number" to request.phoneNumber
                        )
                    )
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
