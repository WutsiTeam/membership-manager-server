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
) : AbstractCsvImportWorkflow<String, CsvImportResponse>(eventStream) {
    override fun doExecute(language: String, context: WorkflowContext): CsvImportResponse {
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

        LocaleContextHolder.setLocale(Locale(language))
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

        return CsvImportResponse(
            imported = imported,
            errors = errors
        )
    }

    private fun doImport(record: CSVRecord) {
        val language = LocaleContextHolder.getLocale().language
        membershipAccessApi.saveCategory(
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
