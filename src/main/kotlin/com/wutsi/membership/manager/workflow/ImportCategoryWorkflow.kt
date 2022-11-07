package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.membership.manager.util.csv.CsvError
import com.wutsi.membership.manager.util.csv.CsvImportResponse
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.net.URL
import java.util.Locale

@Service
class ImportCategoryWorkflow(
    private val mapper: ObjectMapper
) : AbstractWorkflow() {
    companion object {
        const val REQUEST_LANGUAGE = "language"
        const val REQUEST_URL = "url"
    }

    override fun getEventType(): String? = null

    override fun toMemberEventPayload(context: WorkflowContext): MemberEventPayload? = null

    override fun getValidationRules(context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(context: WorkflowContext) {
        val request = context.request as Map<String, String>
        val language = request[REQUEST_LANGUAGE]
        val url = request[REQUEST_URL]

        // Set language
        if (language != null) {
            LocaleContextHolder.setLocale(Locale(language))
        }

        // Load
        context.response = doImport(URL(url))
    }

    private fun doImport(url: URL): CsvImportResponse {
        var row = 1
        var imported = 0
        val errors = mutableListOf<CsvError>()
        val parser = CSVParser.parse(
            url,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("id", "title", "title_fr")
                .build()
        )
        for (record in parser) {
            try {
                doImport(row, record)
                imported++
            } catch (ex: FeignException) {
                errors.add(toCsvError(row, ex))
            } finally {
                row++
            }
        }

        return CsvImportResponse(
            imported = imported,
            errors = errors
        )
    }

    private fun doImport(row: Int, record: CSVRecord) {
        val logger = DefaultKVLogger()
        val language = LocaleContextHolder.getLocale().language

        try {
            logger.add("row", row)
            logger.add("id", record.get("id"))
            logger.add("language", language)

            membershipAccess.saveCategory(
                id = record.get("id").toLong(),
                request = SaveCategoryRequest(
                    title = if (language == "fr") {
                        record.get("title_fr")
                    } else {
                        record.get("title")
                    }
                )
            )
        } catch (ex: Exception) {
            logger.setException(ex)
            throw ex
        } finally {
            logger.log()
        }
    }

    private fun toCsvError(row: Int, ex: FeignException): CsvError =
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
