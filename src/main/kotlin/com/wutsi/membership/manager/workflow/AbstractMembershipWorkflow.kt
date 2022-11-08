package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.AbstractWorkflow
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractMembershipWorkflow(val eventStream: EventStream) :
    AbstractWorkflow<MemberEventPayload>(eventStream) {
    @Autowired
    protected lateinit var membershipAccess: MembershipAccessApi

    protected fun getCurrentAccount(): Account {
        val accountId = SecurityUtil.getAccountId()
        try {
            return membershipAccess.getAccount(accountId).account
        } catch (ex: FeignException.NotFound) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_FOUND.urn,
                    data = mapOf(
                        "account-id" to accountId
                    )
                )
            )
        }
    }
}
