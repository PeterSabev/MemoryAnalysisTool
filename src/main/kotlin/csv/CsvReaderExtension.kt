package csv

import tabular_data.TabularData
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import tabular_data.TabularDataConfiguration
import java.io.File

fun CsvReader.readAllAsTabularData(file: File) : TabularData {
    val valueListList: List<List<String>> = this.readAll(file)
    return Csv(file.path, valueListList)
}