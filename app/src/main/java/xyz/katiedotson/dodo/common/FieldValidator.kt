package xyz.katiedotson.dodo.common

import javax.inject.Inject


class FieldValidator @Inject constructor() {
    fun validateLength(string: String?, min: Int, max: Int): DodoFieldError? {
        return when {
            string == null -> {
                DodoFieldError.Empty
            }
            string.isEmpty() -> {
                DodoFieldError.Empty
            }
            string.length > max -> {
                DodoFieldError.TooLong
            }
            string.length < min -> {
                DodoFieldError.TooShort
            }
            else -> null
        }
    }

    fun validateNotEmpty(string: String?): DodoFieldError? {
        return when {
            string == null -> {
                DodoFieldError.Empty
            }
            string.isEmpty() -> {
                DodoFieldError.Empty
            }
            else -> null
        }
    }

}