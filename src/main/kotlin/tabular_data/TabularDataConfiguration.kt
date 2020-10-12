package tabular_data

import configuration.Configuration

data class TabularDataConfiguration(
    val filePath: String,
    val fieldsNamesToExclude: List<String>,
    val disableNumberOfOccurrencesByFieldValueCounting: Boolean = false
) :
    Configuration