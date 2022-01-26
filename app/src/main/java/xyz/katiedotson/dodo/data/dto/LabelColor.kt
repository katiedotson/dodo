package xyz.katiedotson.dodo.data.dto

object LabelColor {

    enum class LabelColorItem(val hex: String, val colorName: String, val useWhiteText: Boolean, val useBorder: Boolean) {
        PureWhite(hex = "#FFFFFF", colorName = "Pure White", useWhiteText = false, useBorder = true),
        NavajoWhite(hex = "#F9DDAB", colorName = "Navajo White", useWhiteText = false, useBorder = false),
        ChromeYellow(hex = "#F9A706", colorName = "Chrome Yellow", useWhiteText = false, useBorder = false),
        OrangeBlaze(hex = "#FF6704", colorName = "Orange Blaze", useWhiteText = false, useBorder = false),
        CandyApple(hex = "#E7062E", colorName = "Candy Apple", useWhiteText = true, useBorder = false),
        PurplePink(hex = "#A2217E", colorName = "Purple Pink", useWhiteText = true, useBorder = false),
        BlueViolet(hex = "#511296", colorName = "Blue Violet", useWhiteText = true, useBorder = false),
        MidnightBlue(hex = "#020759", colorName = "Midnight Blue", useWhiteText = true, useBorder = false),
        CeruleanBlue(hex = "#114FC6", colorName = "Cerulean Blue", useWhiteText = true, useBorder = false),
        SeaGreen(hex = "#038C5E", colorName = "Sea Green", useWhiteText = true, useBorder = false),
        MantisGreen(hex = "#7FC24F", colorName = "Mantis Green", useWhiteText = false, useBorder = false),
        PureBlack(hex = "#000000", colorName = "Pure Black", useWhiteText = true, useBorder = false)
    }

    fun getAllLabelColors(): List<LabelColorItem> {
        return LabelColorItem.values().toList()
    }

}