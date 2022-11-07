package com.wutsi.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.stream.EventStream
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractWorkflow : Workflow {
    @Autowired
    private lateinit var eventStream: EventStream

    @Autowired
    protected lateinit var membershipAccess: MembershipAccessApi

    protected abstract fun getEventType(): String?
    protected abstract fun toMemberEventPayload(context: WorkflowContext): MemberEventPayload?
    protected abstract fun getValidationRules(context: WorkflowContext): RuleSet
    protected abstract fun doExecute(context: WorkflowContext)

    override fun execute(context: WorkflowContext) {
        validate(context)
        doExecute(context)
        val urn = getEventType()
        if (urn != null) {
            toMemberEventPayload(context)?.let {
                eventStream.publish(urn, it)
            }
        }
    }

    private fun validate(context: WorkflowContext) {
        getValidationRules(context).check()
    }

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
