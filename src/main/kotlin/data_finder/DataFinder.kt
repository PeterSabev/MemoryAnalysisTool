package data_finder

import tabular_data.TabularData
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import csv.readAllAsTabularData
import data_finder.configuration.GroupOfFilesConfiguration
import data_finder.configuration.MemoryDumpConfiguration
import data_finder.configuration.SearchConfiguration
import org.apache.commons.lang3.StringUtils
import tabular_data.TabularDataConfiguration
import java.io.File

class DataFinder(private val applicationId: String, private val searchConfiguration: SearchConfiguration) {
    private lateinit var _searchResultsReport: SearchResultsReport
    val searchResultsReport: SearchResultsReport get() = _searchResultsReport

    private fun loadMemoryDump(configuration: MemoryDumpConfiguration): String {
        return File(configuration.filePath).readText()
    }

    private fun loadTabularData(
        tabularDataConfiguration: TabularDataConfiguration,
        metadataOfFilesToSearchFor: MutableList<TabularData.MetadataFull>
    ): TabularData {
        val tabularData = csvReader().readAllAsTabularData(File(tabularDataConfiguration.filePath))
        metadataOfFilesToSearchFor.add(tabularData.calculateMetadataFull())

        return tabularData
    }

    private fun searchForTabularDataIn(
        groupsOfFilesToSearchFor: List<GroupOfFilesConfiguration<TabularDataConfiguration>>,
        groupOfFilesToSearchIn: GroupOfFilesConfiguration<MemoryDumpConfiguration>
    ): List<SearchResult.DataFoundResult> {
        val dataFondList = ArrayList<SearchResult.DataFoundResult>(groupsOfFilesToSearchFor.size)
        groupOfFilesToSearchIn.files.forEach { memoryDumpConfiguration ->
            val filesPathsExcludedFromNumberOfOccurrencesByFieldValueCounting =  LinkedHashSet<String>()
            val memoryDumpContent = loadMemoryDump(memoryDumpConfiguration)
            groupsOfFilesToSearchFor.forEach { groupOfFilesToSearchFor ->
                val tabularDataConfigurationList = groupOfFilesToSearchFor.files
                val metadataOfFilesToSearchFor = ArrayList<TabularData.MetadataFull>(tabularDataConfigurationList.size)
                val numberOfFieldValuesFoundByFieldName: LinkedHashMap<String, Int> =
                    LinkedHashMap()
                val numberOfOccurrencesByFieldNameAndFieldValue: LinkedHashMap<String, LinkedHashMap<String, Int>> =
                    LinkedHashMap()
                tabularDataConfigurationList.forEach { tabularDataConfiguration ->
                    val tabularData = loadTabularData(tabularDataConfiguration, metadataOfFilesToSearchFor)
                    val fieldNameToExcludeSet = tabularDataConfiguration.fieldsNamesToExclude.toHashSet()
                    if (tabularDataConfiguration.disableNumberOfOccurrencesByFieldValueCounting) {
                        filesPathsExcludedFromNumberOfOccurrencesByFieldValueCounting.add(tabularDataConfiguration.filePath)
                        searchContinuableForTabularDataIn(
                            memoryDumpContent,
                            tabularData,
                            fieldNameToExcludeSet,
                            numberOfFieldValuesFoundByFieldName
                        )
                    } else {
                        searchExtendedContinuableForTabularDataIn(
                            memoryDumpContent,
                            tabularData,
                            fieldNameToExcludeSet,
                            numberOfFieldValuesFoundByFieldName,
                            numberOfOccurrencesByFieldNameAndFieldValue
                        )
                    }
                }
                dataFondList.add(
                    SearchResult.DataFoundResult(
                        groupOfFilesToSearchFor.groupName,
                        numberOfFieldValuesFoundByFieldName.values.sum(),
                        numberOfFieldValuesFoundByFieldName,
                        numberOfOccurrencesByFieldNameAndFieldValue,
                        filesPathsExcludedFromNumberOfOccurrencesByFieldValueCounting,
                        metadataOfFilesToSearchFor
                    )
                )
            }
        }

        return dataFondList
    }


    private fun searchContinuableForTabularDataIn(
        fileContent: String,
        tabularData: TabularData,
        fieldNameToExcludeSet: Set<String>,
        numberOfFieldValuesFoundByFieldName: LinkedHashMap<String, Int>
    ): Map<String, Int> {
        tabularData.fieldNameList.forEach { fieldName ->
            if (fieldNameToExcludeSet.contains(fieldName)) {
                return@forEach
            }

            var numberOfValuesFound = numberOfFieldValuesFoundByFieldName[fieldName] ?: 0

            val tabularDataSize = tabularData.numberOfRows
            for (i in 0 until tabularDataSize) {
                val fieldValue = tabularData[i, fieldName]
                if (fieldValue != null &&
                    fieldValue.isNotBlank() &&
                    fileContent.contains(fieldValue)
                ) {
                    ++numberOfValuesFound
                }
            }
            numberOfFieldValuesFoundByFieldName[fieldName] = numberOfValuesFound
        }
        return numberOfFieldValuesFoundByFieldName
    }

    private fun searchExtendedContinuableForTabularDataIn(
        fileContent: String,
        tabularData: TabularData,
        fieldNameToExcludeSet: Set<String>,
        numberOfFieldValuesFoundByFieldName: LinkedHashMap<String, Int>,
        numberOfOccurrencesByFieldNameAndFieldValue: LinkedHashMap<String, LinkedHashMap<String, Int>>
    ): Map<String, Map<String, Int>> {
        tabularData.fieldNameList.forEach { fieldName ->
            if (fieldNameToExcludeSet.contains(fieldName)) {
                return@forEach
            }

            val numberOfOccurrencesByFieldValue = LinkedHashMap<String, Int>(tabularData.numberOfRows)
            numberOfOccurrencesByFieldNameAndFieldValue[fieldName] = numberOfOccurrencesByFieldValue

            var numberOfValuesFound = numberOfFieldValuesFoundByFieldName[fieldName] ?: 0

            val tabularDataSize = tabularData.numberOfRows
            for (i in 0 until tabularDataSize) {
                val fieldValue = tabularData[i, fieldName]
                if (fieldValue != null &&
                    fieldValue.isNotBlank()
                ) {
                    var fieldValueNumberOfOccurrences = numberOfOccurrencesByFieldValue[fieldValue]
                    if (fieldValueNumberOfOccurrences != null) {

                        fieldValueNumberOfOccurrences += StringUtils.countMatches(fileContent, fieldValue)
                    } else {
                        fieldValueNumberOfOccurrences = StringUtils.countMatches(fileContent, fieldValue)
                    }

                    if (fieldValueNumberOfOccurrences > 0) {
                        numberOfOccurrencesByFieldValue[fieldValue] = fieldValueNumberOfOccurrences
                        ++numberOfValuesFound
                    }
                }
                numberOfFieldValuesFoundByFieldName[fieldName] = numberOfValuesFound
            }
        }
        return numberOfOccurrencesByFieldNameAndFieldValue
    }


    fun search(): SearchResultsReport {
        val searchResults = ArrayList<SearchResult>(searchConfiguration.groupsOfFilesToSearchIn.size)
        searchConfiguration.groupsOfFilesToSearchIn.forEach { groupOfFilesToSearchIn ->
            val dataFondList =
                searchForTabularDataIn(searchConfiguration.groupsOfFilesToSearchFor, groupOfFilesToSearchIn)
            searchResults.add(
                SearchResult(
                    groupOfFilesToSearchIn.groupName,
                    dataFondList
                )
            )
        }
        _searchResultsReport = SearchResultsReport(applicationId, searchResults)
        return _searchResultsReport
    }
}