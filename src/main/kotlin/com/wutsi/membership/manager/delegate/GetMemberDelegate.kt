package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.workflow.GetMemberWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetMemberDelegate(private val workflow: GetMemberWorkflow) {
    public fun invoke(id: Long?): GetMemberResponse {
        return workflow.execute(id, WorkflowContext(id))
    }
}
