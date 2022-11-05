package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class StartMemberRegistrationWorkflow(
    private val membership: MembershipAccessApi,
    private val eventStream: EventStream
) {
    fun execute(phoneNumber: String) {
        val accounts = membership.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = phoneNumber
            )
        ).accounts

        if (accounts.isEmpty()) {
            eventStream.publish(
                EventURN.MEMBER_REGISTRATION_STARTED.urn,
                MemberEventPayload(phoneNumber = phoneNumber)
            )
        } else {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.MEMBER_ALREADY_REGISTERED.urn
                )
            )
        }
    }
}
