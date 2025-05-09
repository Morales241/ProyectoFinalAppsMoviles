package morales.jesus.closetvitual

data class Outfit(
    val id: String = "",
    val nombre: String = "",
    val prendas: Map<String, List<String>> = emptyMap()
)
