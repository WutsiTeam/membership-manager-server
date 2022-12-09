package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.manager.util.csv.CsvError
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractCsvImportWorkflow<Req, Resp>(eventStream: EventStream) :
    AbstractMembershipWorkflow<Req, Resp>(eventStream) {
    @Autowired
    private lateinit var mapper: ObjectMapper

    override fun getEventType(request: Req, response: Resp, context: WorkflowContext): String? = null

    override fun toEventPayload(request: Req, response: Resp, context: WorkflowContext): MemberEventPayload? = null

    override fun getValidationRules(request: Req, context: WorkflowContext) = RuleSet.NONE

    protected fun toCsvError(row: Int, ex: FeignException): CsvError =
        try {
            val response = mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            CsvError(
                row = row,
                code = response.error.code,
                description = response.error.message
            )
        } catch (e: Exception) {
            CsvError(
                row = row,
                code = ex.status().toString(),
                description = ex.message
            )
        }
}
