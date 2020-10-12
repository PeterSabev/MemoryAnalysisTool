package data_finder

import tabular_data.TabularData


data class SearchResult(
    val searchedInGroupName: String,
    val dataFoundResultList: List<DataFoundResult>
) {
    data class DataFoundResult(
        val searchedForGroupName: String,
        val totalNumberOfValuesFound: Int,
        val numberOfFieldValuesFoundByFieldName: Map<String, Int>,
        val numberOfOccurrencesByFieldNameAndFieldValue: Map<String, Map<String, Int>>,
        val filesPathsExcludedFromNumberOfOccurrencesByFieldValueCounting: Set<String>,
        val metadataOfFilesSearchedFor: List<TabularData.MetadataFull>
    )
}