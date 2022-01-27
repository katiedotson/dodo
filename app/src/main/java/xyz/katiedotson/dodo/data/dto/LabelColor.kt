package xyz.katiedotson.dodo.data.dto

object LabelColor {

    enum class LabelColorItem(
        val hex: String,
        val colorName: String,
        val useWhiteText: Boolean,
        val useBorder: Boolean
    ) {
        PureWhite(
            hex = "#FFFFFFFF",
            colorName = "Pure White",
            useWhiteText = false,
            useBorder = true
        ),
        NavajoWhite(
            hex = "#FFF9DDAB",
            colorName = "Navajo White",
            useWhiteText = false,
            useBorder = false
        ),
        ChromeYellow(
            hex = "#FFF9A706",
            colorName = "Chrome Yellow",
            useWhiteText = false,
            useBorder = false
        ),
        OrangeBlaze(
            hex = "#FFFF6704",
            colorName = "Orange Blaze",
            useWhiteText = false,
            useBorder = false
        ),
        CandyApple(
            hex = "#FFE7062E",
            colorName = "Candy Apple",
            useWhiteText = true,
            useBorder = false
        ),
        PurplePink(
            hex = "#FFA2217E",
            colorName = "Purple Pink",
            useWhiteText = true,
            useBorder = false
        ),
        BlueViolet(
            hex = "#FF511296",
            colorName = "Blue Violet",
            useWhiteText = true,
            useBorder = false
        ),
        MidnightBlue(
            hex = "#FF020759",
            colorName = "Midnight Blue",
            useWhiteText = true,
            useBorder = false
        ),
        CeruleanBlue(
            hex = "#FF114FC6",
            colorName = "Cerulean Blue",
            useWhiteText = true,
            useBorder = false
        ),
        SeaGreen(
            hex = "#FF038C5E",
            colorName = "Sea Green",
            useWhiteText = true,
            useBorder = false
        ),
        MantisGreen(
            hex = "#FF7FC24F",
            colorName = "Mantis Green",
            useWhiteText = false,
            useBorder = false
        ),
        PureBlack(
            hex = "#FF000000",
            colorName = "Pure Black",
            useWhiteText = true,
            useBorder = false
        )
    }

    fun getAllLabelColors(): List<LabelColorItem> {
        return LabelColorItem.values().toList()
    }

    fun fromHex(colorHex: String): LabelColorItem? {
        return getAllLabelColors().find {
            it.hex.equals(colorHex, true)
        }
    }

}