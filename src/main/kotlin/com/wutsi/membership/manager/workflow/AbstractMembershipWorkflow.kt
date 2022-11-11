package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.AbstractWorkflow
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractMembershipWorkflow<Req, Resp>(val eventStream: EventStream) :
    AbstractWorkflow<Req, Resp, MemberEventPayload>(eventStream) {
    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    protected fun getCurrentAccount(): Account {
        val accountId = SecurityUtil.getAccountId()
        return membershipAccessApi.getAccount(accountId).account
    }
}
