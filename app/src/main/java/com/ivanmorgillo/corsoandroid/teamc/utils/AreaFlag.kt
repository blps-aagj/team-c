package com.ivanmorgillo.corsoandroid.teamc.utils

@Suppress("ComplexMethod")
fun getFlag(area: String): String {
    return when (area) {
        "American" -> "US"
        "British" -> "GB"
        "Canadian" -> "CA"
        "Chinese" -> "CN"
        "Dutch" -> "NL"
        "Egyptian" -> "EG"
        "French" -> "FR"
        "Greek" -> "GR"
        "Irish" -> "IE"
        "Italian" -> "IT"
        "Jamaican" -> "JM"
        "Japanese" -> "JP"
        "Kenyan" -> "KE"
        "Malaysian" -> "MY"
        "Mexican" -> "MX"
        "Moroccan" -> "MA"
        "Polish" -> "PL"
        "Russian" -> "RU"
        "Spanish" -> "ES"
        "Thai" -> "TH"
        "Tunisian" -> "TN"
        "Turkish" -> "TR"
        "Unknown" -> ""
        "Vietnamese" -> "VN"
        else -> ""
    }
}
