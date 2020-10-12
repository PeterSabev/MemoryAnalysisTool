import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import configuration.FullConfiguration
import data_analyzer.DataAnalyzer
import data_finder.DataFinder
import data_finder.configuration.SearchConfiguration
import java.io.File
import java.lang.reflect.Type


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Configuration file path should be provided!");
        return;
    }

    val listType: Type = object : TypeToken<List<FullConfiguration?>?>() {}.type
    val configurationList: List<FullConfiguration> = Gson().fromJson(File((args[0])).readText(), listType)
    configurationList.forEach { configuration ->
        val dataFinder = DataFinder(configuration.applicationId, configuration.searchConfiguration)
        dataFinder.search()
        val dataAnalyzer = DataAnalyzer(configuration.dataAnalysisConfiguration, dataFinder.searchResultsReport)
        dataAnalyzer.analyze()
    }
}