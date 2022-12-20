package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.workflow.DeactivateBusinessWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeactivateBusinessDelegate(private val workflow: DeactivateBusinessWorkflow) {
    fun invoke() {
        workflow.execute(null, WorkflowContext())
    }
}
