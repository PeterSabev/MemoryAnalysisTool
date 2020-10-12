package tabular_data

interface TabularData : Iterable<List<String>> {
    interface Metadata {
        val filePath: String
        val numberOfRows: Int
        val numberOfFields: Int
        val fieldNameList: List<String>
    }

    interface MetadataFull : Metadata {
        val numberOfNonBlankAndNonZeroValuesByFieldName: Map<String, Int>
        val totalNumberOfNonBlankAndNonZeroValues: Int

        fun numberOfNonBlankAndNonZeroValuesByFieldName(fieldName: String): Int {
            return numberOfNonBlankAndNonZeroValuesByFieldName[fieldName] ?: 0
        }
    }

    val metadata: Metadata

    val filePath: String
        get() = metadata.filePath

    val numberOfRows: Int
        get() = metadata.numberOfRows

    val numberOfFields: Int
        get() = metadata.numberOfFields

    val fieldNameList: List<String>
        get() = metadata.fieldNameList

    operator fun get(rowNumber: Int): List<String>?

    operator fun get(rowNumber: Int, fieldName: String): String?

    operator fun get(fieldName: String): List<String>?

    operator fun get(fieldName: String, rowNumber: Int): String?

    fun calculateMetadataFull() : MetadataFull
}