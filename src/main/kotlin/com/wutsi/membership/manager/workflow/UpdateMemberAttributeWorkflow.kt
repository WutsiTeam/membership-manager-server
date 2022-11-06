package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow(
    private val membership: MembershipAccessApi,
    private val eventStream: EventStream
) {
    fun execute(request: UpdateMemberAttributeRequest) {
        val accountId = SecurityUtil.getAccountId()
        membership.updateAccountAttribute(
            id = accountId,
            request = UpdateAccountAttributeRequest(
                name = request.name,
                value = request.value
            )
        )

        eventStream.publish(
            EventURN.MEMBER_ATTRIBUTE_UPDATED.urn,
            MemberEventPayload(accountId = accountId)
        )
    }
}
