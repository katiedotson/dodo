package xyz.katiedotson.dodo.data.label

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class LabelRepository @Inject constructor(private val labelDao: LabelDao) {

    val labelsFlow: Flow<List<LabelDto>>
        get() = labelDao.getLabelsFlow().transform {
            emit(it.map { label ->
                toDto(label)
            })
        }

    suspend fun createLabel(dto: LabelDto): Long {
        val label = toLabel(dto)
        return labelDao.insertOne(label)
    }

    suspend fun updateLabel(dto: LabelDto): Long {
        val label = toLabel(dto)
        labelDao.updateLabel(label)
        return label.id
    }

    suspend fun deleteLabel(dto: LabelDto) {
        val label = toLabel(dto)
        labelDao.delete(label)
    }

    private fun toDto(label: Label.WithColor): LabelDto {
        return LabelDto(
            labelId = label.labelId,
            labelName = label.labelName,
            colorHex = label.colorHex,
            useWhiteText = label.useWhiteText,
            useBorder = label.useBorder,
            dateCreated = label.dateCreated,
            lastUpdate = label.lastUpdate
        )
    }

    private fun toLabel(dto: LabelDto): Label {
        return Label(id = dto.labelId, name = dto.labelName, colorHex = dto.colorHex, dateCreated = dto.dateCreated, lastUpdate = dto.lastUpdate)
    }

}