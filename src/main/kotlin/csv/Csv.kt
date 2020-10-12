package csv

import tabular_data.TabularData

data class Csv(override val filePath: String, private val valueListList: List<List<String>> ) :
    TabularData {
    data class Metadata(
        override val filePath: String,
        override val numberOfRows: Int,
        override val numberOfFields: Int,
        override val fieldNameList: List<String>
    ) : TabularData.Metadata

    data class MetadataFull(
        override val filePath: String,
        override val numberOfRows: Int,
        override val numberOfFields: Int,
        override val fieldNameList: List<String>,
        override val numberOfNonBlankAndNonZeroValuesByFieldName: Map<String, Int>,
        override val totalNumberOfNonBlankAndNonZeroValues: Int
    ) : TabularData.MetadataFull {
        constructor(
            metadata: Metadata, numberOfNonBlankAndNonZeroValuesByFieldName: Map<String, Int>,
            totalNumberOfNonBlankAndNonZeroValues: Int
        ) : this(
            metadata.filePath,
            metadata.numberOfRows,
            metadata.numberOfFields,
            metadata.fieldNameList,
            numberOfNonBlankAndNonZeroValuesByFieldName,
            totalNumberOfNonBlankAndNonZeroValues
        )
    }

    override val metadata = Metadata(
        filePath,
        valueListList.size - 1,
        valueListList[0].size,
        valueListList[0]
    )

    private val positionOfFieldNameMap: MutableMap<String, Int> = HashMap()

    init {
        fieldNameList.forEachIndexed { index, value -> positionOfFieldNameMap[value] = index }
    }


    override operator fun get(rowNumber: Int): List<String>? {
        return valueListList[rowNumber + 1]
    }

    override operator fun get(rowNumber: Int, fieldName: String): String? {
        val fieldNamePosition = positionOfFieldNameMap[fieldName] ?: return null
        return get(rowNumber)?.get(fieldNamePosition)
    }

    override fun get(fieldName: String): List<String>? {
        TODO("Not yet implemented")
    }

    override fun get(fieldName: String, rowNumber: Int): String? {
        TODO("Not yet implemented")
    }

    override fun calculateMetadataFull(): TabularData.MetadataFull {
        val numberOfNonBlankAndNonZeroValuesByFieldNameMap = calculateNumberOfNonBankAndNonZeroValuesByFieldName()
        return MetadataFull(
            metadata,
            numberOfNonBlankAndNonZeroValuesByFieldNameMap,
            numberOfNonBlankAndNonZeroValuesByFieldNameMap.values.sum()
        )
    }

    private fun calculateNumberOfNonBankAndNonZeroValuesByFieldName(fieldName: String): Int {
        var numberOfNonBlankAndNonZeroValues = 0
        val valueListListSize = valueListList.size
        for (i in 1 until valueListListSize) {
            val fieldNamePosition = positionOfFieldNameMap[fieldName] ?: continue
            val fieldValue = valueListList[i][fieldNamePosition];
            if (fieldValue.isNotBlank() && fieldValue != "0")
                ++numberOfNonBlankAndNonZeroValues
        }

        return numberOfNonBlankAndNonZeroValues
    }

    private fun calculateNumberOfNonBankAndNonZeroValuesByFieldName(): Map<String, Int> {
        val numberOfNonBlankAndNonZeroValuesByFieldName = LinkedHashMap<String, Int>()
        fieldNameList.forEach {
            numberOfNonBlankAndNonZeroValuesByFieldName[it] = calculateNumberOfNonBankAndNonZeroValuesByFieldName(it)
        }

        return numberOfNonBlankAndNonZeroValuesByFieldName
    }

    override fun iterator(): Iterator<List<String>> {
        TODO("Not yet implemented")
    }
}

