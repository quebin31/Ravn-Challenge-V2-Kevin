package co.ravn.kevin.peopleofstarwars

import java.util.*

fun String.customCapitalize(locale: Locale): String {
    return when (this.trim()) {
        "n/a" -> "N/A"
        else -> {
            val words = this.split(',')
            words.joinToString(separator = ", ") { it.trim().capitalize(locale) }
        }
    }
}

const val API_ENDPOINT = "https://swapi-graphql.netlify.app/.netlify/functions/index"