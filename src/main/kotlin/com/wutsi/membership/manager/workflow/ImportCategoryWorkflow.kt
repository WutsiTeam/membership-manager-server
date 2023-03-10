package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import org.springframework.stereotype.Service

@Service
class ImportCategoryWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow {
    override fun execute(context: WorkflowContext) {
        membershipAccessApi.importCategory(context.input as String)
    }
}
