package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.StartMemberRegistrationWorkflow
import com.wutsi.membership.manager.workflow.WorkflowContext
import org.springframework.stereotype.Service
import java.net.URLDecoder

@Service
class StartMemberRegistrationDelegate(private val workflow: StartMemberRegistrationWorkflow) {
    fun invoke(phoneNumber: String) {
        workflow.execute(
            WorkflowContext(
                request = mapOf(
                    StartMemberRegistrationWorkflow.REQUEST_PHONE_NUMBER to URLDecoder.decode(phoneNumber, "utf-8")
                )
            )
        )
    }
}
