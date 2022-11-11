package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.error.ErrorURN
import org.springframework.stereotype.Service

@Service
class StartMemberRegistrationWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<String, Unit>(eventStream) {
    override fun getEventType() = EventURN.MEMBER_REGISTRATION_STARTED.urn

    override fun toEventPayload(phoneNumber: String, response: Unit, context: WorkflowContext) = MemberEventPayload(
        phoneNumber = phoneNumber
    )

    override fun getValidationRules(phoneNumber: String, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(phoneNumber: String, context: WorkflowContext) {
        val accounts = membershipAccessApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = phoneNumber,
                limit = 1
            )
        ).accounts
        if (accounts.isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                    data = mapOf(
                        "phone-number" to phoneNumber
                    )
                )
            )
        }
    }
}
