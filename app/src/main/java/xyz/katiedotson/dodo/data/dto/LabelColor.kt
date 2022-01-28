package xyz.katiedotson.dodo.data.dto

object LabelColor {

    enum class LabelColorItem(
        val hex: String,
        val displayName: String,
        val useWhiteText: Boolean,
        val useBorder: Boolean
    ) {
        PureWhite(
            hex = "#FFFFFFFF",
            displayName = "Pure White",
            useWhiteText = false,
            useBorder = true
        ),
        NavajoWhite(
            hex = "#FFF9DDAB",
            displayName = "Navajo White",
            useWhiteText = false,
            useBorder = false
        ),
        ChromeYellow(
            hex = "#FFF9A706",
            displayName = "Chrome Yellow",
            useWhiteText = false,
            useBorder = false
        ),
        OrangeBlaze(
            hex = "#FFFF6704",
            displayName = "Orange Blaze",
            useWhiteText = false,
            useBorder = false
        ),
        CandyApple(
            hex = "#FFE7062E",
            displayName = "Candy Apple",
            useWhiteText = true,
            useBorder = false
        ),
        PurplePink(
            hex = "#FFA2217E",
            displayName = "Purple Pink",
            useWhiteText = true,
            useBorder = false
        ),
        BlueViolet(
            hex = "#FF511296",
            displayName = "Blue Violet",
            useWhiteText = true,
            useBorder = false
        ),
        MidnightBlue(
            hex = "#FF020759",
            displayName = "Midnight Blue",
            useWhiteText = true,
            useBorder = false
        ),
        CeruleanBlue(
            hex = "#FF114FC6",
            displayName = "Cerulean Blue",
            useWhiteText = true,
            useBorder = false
        ),
        SeaGreen(
            hex = "#FF038C5E",
            displayName = "Sea Green",
            useWhiteText = true,
            useBorder = false
        ),
        MantisGreen(
            hex = "#FF7FC24F",
            displayName = "Mantis Green",
            useWhiteText = false,
            useBorder = false
        ),
        PureBlack(
            hex = "#FF000000",
            displayName = "Pure Black",
            useWhiteText = true,
            useBorder = false
        )
    }

    fun getAllLabelColors(): List<LabelColorItem> {
        return LabelColorItem.values().toList()
    }

    fun fromHex(colorHex: String): LabelColorItem {
        return getAllLabelColors().find {
            it.hex.equals(colorHex, true)
        } ?: LabelColorItem.PureWhite
    }

}