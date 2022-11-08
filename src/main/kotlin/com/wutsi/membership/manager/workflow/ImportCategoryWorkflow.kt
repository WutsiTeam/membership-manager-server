package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.membership.manager.util.csv.CsvError
import com.wutsi.membership.manager.util.csv.CsvImportResponse
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.net.URL
import java.util.Locale

@Service
class ImportCategoryWorkflow(
    eventStream: EventStream,
    @Value("\${wutsi.application.services.category.url}") private val csvUrl: String
) : AbstractCsvImportWorkflow(eventStream) {
    companion object {
        const val REQUEST_LANGUAGE = "language"
    }

    override fun doExecute(context: WorkflowContext) {
        var row = 1
        var imported = 0
        val errors = mutableListOf<CsvError>()
        val parser = CSVParser.parse(
            URL(csvUrl),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("id", "title", "title_fr")
                .build()
        )

        setImportLanguage(context)
        for (record in parser) {
            val logger = DefaultKVLogger()
            log(row, record, logger)
            try {
                doImport(record)
                imported++
            } catch (ex: FeignException) {
                errors.add(toCsvError(row, ex))
                logger.setException(ex)
            } finally {
                logger.log()
                row++
            }
        }

        context.response = CsvImportResponse(
            imported = imported,
            errors = errors
        )
    }

    private fun setImportLanguage(context: WorkflowContext) {
        val language = (context.request as Map<String, String?>)[REQUEST_LANGUAGE]
        if (language != null) {
            LocaleContextHolder.setLocale(Locale(language))
        }
    }

    private fun doImport(record: CSVRecord) {
        val language = LocaleContextHolder.getLocale().language
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
    }

    private fun log(row: Int, record: CSVRecord, logger: KVLogger) {
        logger.add("row", row)
        logger.add("record_id", record.get("id"))
        logger.add("record_title", record.get("title"))
        logger.add("record_title_fr", record.get("title_fr"))
    }
}
