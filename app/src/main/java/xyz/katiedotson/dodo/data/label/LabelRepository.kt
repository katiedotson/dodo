package xyz.katiedotson.dodo.data.label

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LabelRepository @Inject constructor(private val labelDao: LabelDao) {

    val labelsFlow: Flow<List<Label>>
        get() = labelDao.getLabelsFlow()

    suspend fun createLabel(label: Label): Long {
        return labelDao.insertOne(label)
    }

    suspend fun updateLabel(label: Label): Long {
        labelDao.updateLabel(label)
        return label.id
    }

    suspend fun deleteLabel(label: Label) {
        labelDao.delete(label)
    }

}