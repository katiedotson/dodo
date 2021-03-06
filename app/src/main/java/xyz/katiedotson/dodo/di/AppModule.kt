package xyz.katiedotson.dodo.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.katiedotson.dodo.BuildConfig
import xyz.katiedotson.dodo.data.DodoDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDodoDatabase(@ApplicationContext app: Context): DodoDatabase =
        Room.databaseBuilder(app, DodoDatabase::class.java, "dodo").fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideTodoDao(db: DodoDatabase) = db.todoDao()

    @Singleton
    @Provides
    fun provideLabelDao(db: DodoDatabase) = db.labelDao()

    @Singleton
    @Provides
    fun provideColorDao(db: DodoDatabase) = db.colorDao()

    @Singleton
    @Provides
    fun provideUserSettingsDao(db: DodoDatabase) = db.userSettingsDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("${BuildConfig.APPLICATION_ID}.shared_preferences", Context.MODE_PRIVATE)
    }
}