package configuration

import data_analyzer.configuration.DataAnalysisConfiguration
import data_finder.configuration.SearchConfiguration

data class FullConfiguration(
    val applicationId: String,
    val searchConfiguration: SearchConfiguration,
    val dataAnalysisConfiguration: DataAnalysisConfiguration
)