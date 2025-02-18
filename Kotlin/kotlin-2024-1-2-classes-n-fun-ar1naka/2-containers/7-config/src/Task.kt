import java.io.InputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Config(fileName: String) {
    private val map: Map<String, String> = extractContent(fileName)

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
        ReadOnlyProperty<Any?, Any> { _, _ -> map.getValue(property.name) }.also {
            require(property.name in map) { "${property.name}' not found in map!" }
        }

    companion object {
        private fun extractContent(fileName: String): MutableMap<String, String> {
            val map = mutableMapOf<String, String>()
            getResource(fileName)?.bufferedReader()?.forEachLine { line ->
                val parts = line.split("=")
                map[parts[0].trim()] = parts[1].trim()
            } ?: throw IllegalArgumentException("Resource don't exist!")
            return map
        }
    }
}

@Suppress(
    "RedundantNullableReturnType",
    "UNUSED_PARAMETER",
)
fun getResource(fileName: String): InputStream? {
    // do not touch this function
    val content =
        """
        |valueKey = 10
        |otherValueKey = stringValue 
        """.trimMargin()

    return content.byteInputStream()
}
