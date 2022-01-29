package xyz.katiedotson.dodo.common


class Event<out T>(val content: T) {
    //Creates a flag to track if data has been observed
    private var hasBeenHandled = false

    // Creates a function that you’ll invoke from outside world to get the data.
    // This returns null if data has already been observed else returns the data.
    // Also, it sets hasBeenHandled to true to block further reacting on data.
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled)
            null
        else {
            hasBeenHandled = true
            content
        }
    }

}
