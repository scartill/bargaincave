package ru.bargaincave.warehouse

class FruitInfo {
    companion object {
        val Sorts = mapOf(
            "Mango" to listOf("Kent", "Keitt"),
            "Avocado" to listOf("Fuerte", "Hass")
        )

        val Origins = listOf(
            "Peru",
            "Africa",
            "Egypt",
            "Israel",
            "Colombia",
            "Kenya",
            "Venezuela",
            "Brasil"
        )

        val Conditions = listOf(
            "Ripe",
            "Green"
        )

        val Calibers = 6..32
    }
}
