package com.wutsi.membership.manager.workflow.task

import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LogoutPasswordTask(
    private val workflowEngine: WorkflowEngine,
    private val securityManagerApi: SecurityManagerApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "logout")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        securityManagerApi.logout()
    }
}
