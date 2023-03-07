package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class ImportCategoryWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow<String, Unit> {
    override fun execute(language: String, context: WorkflowContext) {
        membershipAccessApi.importCategory(language)
    }
}
