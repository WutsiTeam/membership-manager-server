package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractMembershipWorkflow<Req, Resp>(val eventStream: EventStream) :
    AbstractWorkflow<Req, Resp, MemberEventPayload>(eventStream) {
    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    protected fun getCurrentAccountId(context: WorkflowContext): Long =
        context.accountId ?: SecurityUtil.getAccountId()

    protected fun getCurrentAccount(context: WorkflowContext): Account {
        val accountId = getCurrentAccountId(context)
        return getAccount(accountId)
    }

    protected fun getAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account
}
