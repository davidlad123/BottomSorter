package com.zphinx.sortsearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sort_dialog.*

/**
 * The dialog which is displayed by the sort attributes system
 * @property title String
 * @property positionButton String
 * @property negativeButton String
 * @property adapter SortAdapter
 * @property canCancel Boolean
 * @property isTwoButton Boolean
 * @property positiveFunction Function0<Unit>
 * @property negativeFunction Function0<Unit>
 * @property listView ListView
 */
class SortDialogFragment : BottomSheetDialogFragment() {

    private lateinit var title: String
    private lateinit var positionButton: String
    private lateinit var negativeButton: String
    private lateinit var adapter: SortDialogManager.SortAdapter
    private var canCancel: Boolean = false
    private var isTwoButton: Boolean = true
    var positiveFunction: () -> Unit = {}
    var negativeFunction: () -> Unit = {}
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments!!.getString("title", "")
        positionButton = arguments!!.getString("positionButton", "")
        negativeButton = arguments!!.getString("negativeButton", "")
        isTwoButton = arguments!!.getBoolean("isTwoButton")
        canCancel = arguments!!.getBoolean("canCancel")
        setStyle(STYLE_NO_TITLE, 0)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = R.layout.sort_dialog
        val view = inflater.inflate(layout, container)
        listView = view.findViewById(R.id.sort_list) as ListView
        listView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogTitle.text = title
        isCancelable = canCancel
        negative_button.text = negativeButton
        positive_button.text = positionButton

        if (!isTwoButton) {
            negative_button.visibility = View.INVISIBLE
        }

        setButtonListeners()
    }

    private fun setButtonListeners() {
        val negativeListener = View.OnClickListener {
            negativeFunction.invoke()
            dismissAllowingStateLoss()
        }
        val positiveListener = View.OnClickListener {
            positiveFunction.invoke()
            dismissAllowingStateLoss()
        }
        negative_button.setOnClickListener(negativeListener)
        positive_button.setOnClickListener(positiveListener)
    }

    override fun show(manager: androidx.fragment.app.FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            Log.d("SortDialogFragment", "Exception: ${e.message}")
        }
    }


    companion object {
        fun newInstance(
            t: String,
            positionButton: String,
            negativeButton: String,
            canCancel: Boolean,
            positiveFunction: () -> Unit,
            negativeFunction: () -> Unit,
            someAdapter: SortDialogManager.SortAdapter
        ): SortDialogFragment {
            val f = SortDialogFragment()
            val args  = Bundle().apply{
                    putString("title", t)
                    putString("positionButton", positionButton)
                    putString("negativeButton", negativeButton)
                    putBoolean("isTwoButton", negativeButton!=null)
                    putBoolean("canCancel", canCancel)
                }

            f.arguments = args
            f.adapter = someAdapter
            f.positiveFunction = positiveFunction
            f.negativeFunction = negativeFunction
            return f
        }
    }
}
