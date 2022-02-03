package xyz.katiedotson.dodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.katiedotson.dodo.data.color.ColorDao
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.data.label.LabelDao
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.data.todo.TodoDao

@Database(entities = [Todo::class, Label::class, DodoColor::class], views=[Todo.WithLabel::class, Label.WithColor::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun labelDao(): LabelDao
    abstract fun colorDao(): ColorDao
}

