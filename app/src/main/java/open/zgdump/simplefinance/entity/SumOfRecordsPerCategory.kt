package open.zgdump.simplefinance.entity

import androidx.room.Entity
import kotlinx.datetime.LocalDate

@Entity
data class SumOfRecordsPerCategory(
    val sum: Int,
    val currencyDesignation: String,
    val categoryId: Int,
    val categoryName: String,
)