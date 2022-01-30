package xyz.katiedotson.dodo.data.color

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ColorRepository @Inject constructor(private val colorDao: ColorDao) {

    val colors: Flow<List<DodoColor>> = colorDao.getColorsFlow()

    suspend fun deleteAll() {
        colorDao.deleteAll()
    }

    suspend fun insertColors(colors: List<DodoColor>) {
        colorDao.insertAll(colors)
    }

}