package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.workflow.GetMemberByNameWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetMemberByNameDelegate(private val workflow: GetMemberByNameWorkflow) {
    public fun invoke(name: String): GetMemberResponse {
        return workflow.execute(name, WorkflowContext())
    }
}
