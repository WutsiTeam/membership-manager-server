package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.workflow.StartMemberRegistrationWorkflow
import org.springframework.stereotype.Service
import java.net.URLDecoder

@Service
class StartMemberRegistrationDelegate(private val workflow: StartMemberRegistrationWorkflow) {
    fun invoke(phoneNumber: String) {
        workflow.execute(URLDecoder.decode(phoneNumber, "utf-8"))
    }
}
