package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.DeleteMemberWorkflow
import com.wutsi.membership.manager.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class DeleteMemberDelegate(private val workflow: DeleteMemberWorkflow) {
    public fun invoke() {
        workflow.execute(WorkflowContext())
    }
}
