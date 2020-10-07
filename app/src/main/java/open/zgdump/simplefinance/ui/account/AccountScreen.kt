package open.zgdump.simplefinance.ui.account

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_new_account.view.*
import kotlinx.android.synthetic.main.fragment_account.*
import moxy.ktx.moxyPresenter
import open.zgdump.simplefinance.R
import open.zgdump.simplefinance.entity.Account
import open.zgdump.simplefinance.entity.Currency
import open.zgdump.simplefinance.presentation.account.AccountScreenPresenter
import open.zgdump.simplefinance.presentation.account.AccountScreenView
import open.zgdump.simplefinance.presentation.global.Paginator
import open.zgdump.simplefinance.ui.global.paginal.PaginalFragment
import open.zgdump.simplefinance.util.kotlin.argument

class AccountScreen :
    PaginalFragment<AccountScreenView, Account>(R.layout.fragment_account),
    AccountScreenView {

    override val mainPresenter by moxyPresenter { AccountScreenPresenter(isSaving) }

    private val isSaving: Boolean by argument(ARG_IS_SAVING, false)

    companion object {
        private const val ARG_IS_SAVING = "is_saving"
        fun create(isSaving: Boolean) =
            AccountScreen().apply {
                arguments = bundleOf(ARG_IS_SAVING to isSaving)
            }
    }


    override val adapterDelegate: AdapterDelegate<MutableList<Any>>
        get() = AccountAdapterDelegate(mainPresenter::itemClicked)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupPaginalRenderView(paginalRenderView)
    }

    override fun renderPaginatorState(state: Paginator.State) {
        paginalRenderView.render(state)
    }

    override fun showMessage(message: String) {
        Toasty.info(activity, message).show()
    }

    override fun newAccountDialog(account: Account?, currencies: List<Currency>) {
        MaterialDialog(activity, ModalDialog).show {

            val isEdit = account != null
            val title =
                if (isEdit)
                    R.string.AccountScreen_editCurrencyDialogTitle
                else
                    R.string.AccountScreen_newCurrencyDialogTitle

            // Содержимое
            title(title)
            customView(
                R.layout.dialog_new_account,
                scrollable = true,
                horizontalPadding = true
            )

            // Настройка кнопок
            positiveButton(android.R.string.ok) { d ->
                newAccountDialogComplete(d.getCustomView(), account, currencies)
            }
            negativeButton(android.R.string.cancel)

            // Настройка содержимого
            getCustomView().apply {
                nameEditText.setText(account?.name ?: "")
                amountEditText.setText(account?.amount?.toString() ?: 0.0.toString())
                currencySpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    currencies.map { "${it.name}, ${it.designation}" }
                )
                isSavingCheckBox.isChecked = account?.isSaving ?: isSaving
                isClosedCheckBox.isChecked = account?.isClosed ?: false
            }
        }
    }

    private fun newAccountDialogComplete(
        dialogView: View,
        originalAccount: Account?,
        currencies: List<Currency>
    ) {
        mainPresenter.accountDialogComplete(
            originalAccount,
            dialogView.nameEditText.text.toString(),
            dialogView.amountEditText.text.toString().toFloat(),
            currencies[dialogView.currencySpinner.selectedItemPosition],
            dialogView.isSavingCheckBox.isChecked,
            dialogView.isClosedCheckBox.isChecked,
        )
    }
}