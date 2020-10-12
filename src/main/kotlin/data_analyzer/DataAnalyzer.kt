package data_analyzer

import com.google.gson.Gson
import data_analyzer.configuration.DataAnalysisConfiguration
import data_finder.SearchResult
import data_finder.SearchResultsReport
import java.io.File


class DataAnalyzer(private val dataAnalysisConfiguration: DataAnalysisConfiguration, private val searchResultsReport: SearchResultsReport) {
    private lateinit var _dataAnalysisOutputResultJson: String
    val dataAnalysisOutputResultJson get() = _dataAnalysisOutputResultJson
    fun analyze() {
        _dataAnalysisOutputResultJson = Gson().toJson(searchResultsReport)
        File(dataAnalysisConfiguration.dataAnalysisOutputResult).writeText(_dataAnalysisOutputResultJson)
    }
}