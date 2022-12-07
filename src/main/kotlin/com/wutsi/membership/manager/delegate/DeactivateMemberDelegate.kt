package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.DeactivateMemberWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeactivateMemberDelegate(private val workflow: DeactivateMemberWorkflow) {
    fun invoke() {
        workflow.execute(null, WorkflowContext())
    }
}
