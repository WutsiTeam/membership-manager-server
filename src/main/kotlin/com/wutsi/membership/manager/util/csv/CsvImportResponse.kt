package com.wutsi.membership.manager.util.csv

data class CsvImportResponse(
    val imported: Int = 0,
    val errors: List<CsvError> = emptyList(),
)
