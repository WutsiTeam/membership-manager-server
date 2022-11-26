package com.wutsi.membership.manager.workflow

import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dto.SavePlaceRequest
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
import org.springframework.stereotype.Service
import java.net.URL

@Service
class ImportPlaceWorkflow(
    eventStream: EventStream,
    @Value("\${wutsi.application.services.place.url-prefix}") private val csvUrlPrefix: String,
    @Value("\${wutsi.application.services.place.min-population}") private val minPopulation: Int
) : AbstractCsvImportWorkflow<String, CsvImportResponse>(eventStream) {
    companion object {
        private const val RECORD_ID = 0
        private const val RECORD_NAME = 1
        private const val RECORD_LATITUDE = 4
        private const val RECORD_LONGITUDE = 5
        private const val RECORD_FEATURE_CLASS = 6
        private const val RECORD_FEATURE_CODE = 7
        private const val RECORD_COUNTRY = 8
        private const val RECORD_POPULATION = 14
        private const val RECORD_TIMEZONE = 17
    }

    override fun doExecute(country: String, context: WorkflowContext): CsvImportResponse {
        var row = 1
        var imported = 0
        val errors = mutableListOf<CsvError>()
        val parser = CSVParser.parse(
            URL("$csvUrlPrefix/$country.txt"),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter("\t")
                .build()
        )

        for (record in parser) {
            try {
                if (accept(record, country)) {
                    val logger = DefaultKVLogger()
                    log(row, record, logger)
                    doImport(record)
                    imported++
                    logger.log()
                }
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

    private fun accept(record: CSVRecord, country: String): Boolean {
        return country == record.get(RECORD_COUNTRY) &&
            record.get(RECORD_FEATURE_CLASS) == "P" &&
            listOf(
                "PPL",
                "PPLC",
                "PPLA",
                "PPLA2",
                "PPLA3",
                "PPLA4",
                "PPLA5"
            ).contains(record.get(RECORD_FEATURE_CODE)) &&
            record.get(RECORD_POPULATION) != null &&
            record.get(RECORD_POPULATION).toInt() > minPopulation
    }

    private fun doImport(record: CSVRecord) {
        membershipAccessApi.savePlace(
            request = SavePlaceRequest(
                id = record.get(RECORD_ID).toLong(),
                name = record.get(RECORD_NAME),
                country = record.get(RECORD_COUNTRY),
                type = PlaceType.CITY.name,
                longitude = record.get(RECORD_LONGITUDE).toDouble(),
                latitude = record.get(RECORD_LATITUDE).toDouble(),
                timezoneId = record.get(RECORD_TIMEZONE)
            )
        )
    }

    private fun log(row: Int, record: CSVRecord, logger: KVLogger) {
        logger.add("row", row)
        logger.add("record_id", record.get(RECORD_ID))
        logger.add("record_name", record.get(RECORD_NAME))
        logger.add("record_feature_class", record.get(RECORD_FEATURE_CLASS))
        logger.add("record_feature_code", record.get(RECORD_FEATURE_CODE))
        logger.add("record_population", record.get(RECORD_POPULATION))
        logger.add("record_latitude", record.get(RECORD_LATITUDE))
        logger.add("record_longitude", record.get(RECORD_LONGITUDE))
        logger.add("record_timezone", record.get(RECORD_TIMEZONE))
    }
}
