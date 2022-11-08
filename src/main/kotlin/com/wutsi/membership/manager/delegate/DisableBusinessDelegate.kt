package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.DisableBusinessWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class DisableBusinessDelegate(private val workflow: DisableBusinessWorkflow) {
    public fun invoke() {
        workflow.execute(WorkflowContext())
    }
}
