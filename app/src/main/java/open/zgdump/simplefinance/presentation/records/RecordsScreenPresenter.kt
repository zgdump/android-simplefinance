package open.zgdump.simplefinance.presentation.records

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import open.zgdump.simplefinance.App
import open.zgdump.simplefinance.entity.FinancialTypeTransaction
import open.zgdump.simplefinance.presentation.global.MvpPresenterX

class RecordsScreenPresenter(
    private val type: FinancialTypeTransaction
) : MvpPresenterX<RecordsScreenView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showTotalSum()
    }

    private fun showTotalSum() {
        viewState.showSumOfRecords(runBlocking {
            App.db.recordDao().getSumOfRecords(
                LocalDate(2020, 10, 1),
                LocalDate(2020, 10, 30),
                type
            ) ?: 0.0f
        })
    }
}