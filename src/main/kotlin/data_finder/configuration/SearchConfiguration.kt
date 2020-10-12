package data_finder.configuration

import configuration.Configuration
import tabular_data.TabularDataConfiguration

data class SearchConfiguration(
    val groupsOfFilesToSearchFor: List<GroupOfFilesConfiguration<TabularDataConfiguration>>,
    val groupsOfFilesToSearchIn: List<GroupOfFilesConfiguration<MemoryDumpConfiguration>>
) : Configuration