package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.workflow.task.DeactivatePaymentMethod
import com.wutsi.membership.manager.workflow.task.DeletePasswordTask
import com.wutsi.membership.manager.workflow.task.LogoutPasswordTask
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DeleteMemberWorkflow(
    private val workflowEngine: WorkflowEngine,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "delete-member")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        deactivateAccount(context)
        deletePassword(context)
        deactivatePaymentMethod(context)
        logout(context)
    }

    private fun deactivateAccount(context: WorkflowContext) =
        membershipAccessApi.updateAccountStatus(
            id = context.accountId!!,
            request = UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name,
            ),
        )

    private fun deletePassword(context: WorkflowContext) =
        workflowEngine.executeAsync(DeletePasswordTask.ID, context)

    private fun deactivatePaymentMethod(context: WorkflowContext) =
        workflowEngine.executeAsync(DeactivatePaymentMethod.ID, context)

    private fun logout(context: WorkflowContext) =
        workflowEngine.executeAsync(LogoutPasswordTask.ID, context)
}
