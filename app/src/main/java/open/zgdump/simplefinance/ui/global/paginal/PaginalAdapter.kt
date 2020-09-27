package open.zgdump.simplefinance.ui.global.paginal

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

/**
 * Created by petrova_alena on 2019-12-05.
 */
class PaginalAdapter(
    private val nextPageCallback: () -> Unit,
    itemDiff: (old: Any, new: Any) -> Boolean,
    vararg delegate: AdapterDelegate<MutableList<Any>>
) : AsyncListDifferDelegationAdapter<Any>(
    DummyDiffItemCallback(
        itemDiff
    )
) {

    var fullData = false
    private var nextPageCaught = false

    init {
        items = mutableListOf()

        delegatesManager.addDelegate(ProgressAdapterDelegate())
        delegate.forEach { delegatesManager.addDelegate(it) }
    }

    fun update(data: List<Any>, isPageProgress: Boolean) {
        nextPageCaught = false
        items = mutableListOf<Any>().apply {
            addAll(data)
            if (isPageProgress) add(ProgressItem)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any?>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (!fullData && position >= items.size - 10) {
            if (!nextPageCaught) {
                nextPageCaught = true
                nextPageCallback.invoke()
            }
        }
    }
}
