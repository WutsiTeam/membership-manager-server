package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.manager.dto.RegisterMemberRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.PhoneUtil
import com.wutsi.platform.core.stream.EventStream
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class RegisterMemberWorkflow(
    private val membership: MembershipAccessApi,
    private val eventStream: EventStream
) {
    fun execute(request: RegisterMemberRequest) {
        val accountId = membership.createAccount(
            request = CreateAccountRequest(
                phoneNumber = request.phoneNumber,
                displayName = request.displayName,
                country = PhoneUtil.detectCountry(request.phoneNumber),
                language = LocaleContextHolder.getLocale().language
            )
        ).accountId

        eventStream.publish(
            EventURN.MEMBER_REGISTERED.urn,
            MemberEventPayload(
                accountId = accountId,
                pin = request.pin,
                phoneNumber = request.phoneNumber
            )
        )
    }
}
