/**
 * SortDialogManager.java
 * Created: 8 Feb 2013 20:36:26
 *
 * @author David Ladapo
 * Copyright () 2011  Zphinx Software Solutions
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * License for more details.
 *
 *
 * THERE IS NO WARRANTY FOR THIS SOFTWARE, TO THE EXTENT PERMITTED BY
 * APPLICABLE LAW.  EXCEPT WHEN OTHERWISE STATED IN WRITING BY ZPHINX SOFTWARE SOLUTIONS
 * AND/OR OTHER PARTIES WHO PROVIDE THIS SOFTWARE "AS IS" WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM
 * IS WITH YOU.  SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF
 * ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 *
 * IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING
 * WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS
 * THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY
 * GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE
 * USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF
 * DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD
 * PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS),
 * EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGES.
 *
 *
 * For further information, please go to http://www.zphinx.co.uk/
 */
package com.zphinx.sortsearch

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat


/**
 * Manages the dialog for the Sort dialog used by this app.
 *
 * @author David Ladapo Created: 8 Feb 2013 20:36:26
 * @version 1.0 Copyright () 2011 Zphinx Software Solutions
 */
class SortDialogManager {
    private var sortFargment: SortDialogFragment? = null
    private var selectedIndex = 0
    /**
     * Initializes and displays the alert dialog hosting the list of
     * StateTextView objects
     *
     * @param activity
     * - The activity which uses this dialog
     */
    fun showAlertDialog(activity: AppCompatActivity, fireSorter: () -> Unit) {

        if (sortFargment == null) {
            val res: Resources = activity.resources
            val sortStrings: Array<String?> = res.getStringArray(R.array.searchSortValues)
            val title = activity.getString(R.string.sort_search_results)

            val adapter = SortAdapter(
                this,
                activity,
                R.layout.spinner_sort_list,
                sortStrings
            )
            sortFargment = SortDialogFragment.newInstance(
                title,
                activity.getString(R.string.ok),
                activity.getString(R.string.cancel),
                true,
                canCancel = true,
                positiveFunction = { sortListener(adapter, activity, fireSorter) },
                negativeFunction = ::callDismiss,
                bundle = null,
                someAdapter = adapter
            )

        }
        // Showing dialog
        sortFargment?.show(activity.supportFragmentManager, "sorter")
    }

    private fun callDismiss() {
        sortFargment?.dismiss()
    }

    /**
     * An adapter used by this dialog manager to parse its list of StateTextview
     * objects
     *
     * @author David Ladapo Created: 17 Aug 2013 15:38:48
     * @version 1.0 Copyright () 2013
     */
    class SortAdapter(private val manager: SortDialogManager, val activity: AppCompatActivity?, textResourceId: Int, options: Array<String?>?) :
        ArrayAdapter<StateTextView?>(activity?.baseContext!!, textResourceId) {
        private var textResourceId = 0
        private var options: Array<String?>? = null


        init {
            this.textResourceId = textResourceId
            this.options = options
            stateTextViews = arrayOfNulls<StateTextView?>(options!!.size)
            notifyDataSetChanged()
        }

        override fun getCount(): Int = options?.size ?: 0

        override fun getItem(position: Int): StateTextView? {
            return when {
                !isEmpty && position > -1 -> {
                    stateTextViews!![position]
                }
                else -> null
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            if (stateTextViews!![position] == null) {
                val inflater: LayoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: StateTextView =
                    inflater.inflate(textResourceId, parent, false) as StateTextView
                rowView.text = options!![position]
                val vhs = ViewHolder()
                vhs.stateView = rowView
                rowView.tag = vhs
                rowView.setOnClickListener(manager.createItemListener(activity, this, position))

                stateTextViews!![position] = rowView
                if (activity is SortProperty) {

                    if (position == activity.sortIndex) {

                        if (activity.sortDirection > 0) {
                            delayedInit(position, activity)

                        }
                    }
                }
            }
            return stateTextViews!![position]!!
        }

        private fun delayedInit(position: Int, activity: SortProperty) {
            Handler().postDelayed(
                {
                initStateTextView(stateTextViews!![position]!!)
                    stateTextViews!![position]!!.sortDirection = activity.sortDirection
                    drawStateTextView(
                        activity.sortDirection,
                        stateTextViews!![position]?.compoundDrawables,
                        (activity as AppCompatActivity).resources,
                        stateTextViews!![position]!!
                    )

                }, 500
            )
        }


        fun initStateTextView(anon: StateTextView?) {
            anon?.let { stv ->
                stv.setCompoundDrawables(null, null, null, null)
                stv.sortDirection = StateTextView.SORT_UNINITIALIZED
            }
        }

        private inner class ViewHolder {
            @SuppressWarnings("unused")
            var stateView: StateTextView? = null
        }

        companion object {
            var stateTextViews: Array<StateTextView?>? = null
        }
    }

    /**
     * Creates an ItemListener used by the List adapter
     *
     * @param activity
     * - The activity which uses this dialog
     * @param adapter
     * - The adapter associated with the list of state textview
     * objects
     * @return A instance of an onClickListener used by the stated list adapter
     */
    fun createItemListener(activity: AppCompatActivity?, adapter: ListAdapter?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            clickStateTextView(activity, adapter, position)
        }
    }

    /**
     * Specifies the actions that ovccur when a stateTextView object is clicked
     *
     * @param activity
     * The activity hosting this dialogManager
     * @param adapter
     * The list adapter containing multiple stateview
     * @param position
     * The position of the stateview object been clicked
     * @throws NotFoundException
     * If the specified state textview is not available
     */
    @Throws(NotFoundException::class)
    fun clickStateTextView(activity: AppCompatActivity?, adapter: ListAdapter?, position: Int) {
        if (position > -1) {
            adapter?.let {
                val count: Int = it.count
                preInitStateTextView(count, position, it)
                val rowView: StateTextView = it.getItem(position) as StateTextView
                val draws: Array<Drawable?> = rowView.compoundDrawables

                Log.d(TAG, "The stateView is: .........................$rowView")
                Log.d(TAG, "The drawable is is: ........................." + draws[2])
                drawImage(activity, rowView, draws)

                selectedIndex = position
            }
        }
    }

    private fun drawImage(
        activity: AppCompatActivity?,
        rowView: StateTextView,
        draws: Array<Drawable?>
    ) {
        activity?.let { it ->
            setCurrentImage(
                rowView,
                draws,
                it
            )
        }
    }

    private fun preInitStateTextView(
        count: Int,
        position: Int,
        it: ListAdapter
    ) {
        for (i in 0 until count) {
            if (i != position) {

                val anon: StateTextView? = it.getItem(i) as StateTextView
                (it as SortAdapter).initStateTextView(anon)
            }
        }
    }


    /**
     * A listener used to fire the sorting action promoted by this dialog
     *
     * @param adapter
     * The adapter associated with the list of state textview objects
     * @return A click listener used to sort data in the associated activity
     */
    private fun sortListener(
        adapter: ListAdapter?,
        activity: AppCompatActivity?,
        fireSorter: () -> Unit
    ) {
        val stv: StateTextView? = adapter?.getItem(selectedIndex) as StateTextView
        Log.d(TAG, "The stateView is: .." + stv + "at position: " + selectedIndex)
        stv?.let {
            val direction: Int = it.sortDirection
            if (activity is SortProperty) {
                activity.processSorting(selectedIndex, direction, fireSorter)
            }
        }
        sortFargment?.dismiss()

    }

    companion object {
        private val TAG: String? = "SortDialogManager"
        /**
         * Sets the image to be shown by the drawable associated with the
         * StateTextview
         *
         * @param text
         * The state textview object whose image is to be set
         * @param draws
         * The array of drawables used by the textview
         * @param activity
         * - The activity which uses this dialog
         * @throws NotFoundException
         * If an error occurs
         */
        @Throws(NotFoundException::class)
        private fun setCurrentImage(
            text: StateTextView,
            draws: Array<Drawable?>?,
            activity: AppCompatActivity
        ) {
            val res: Resources = activity.resources
            val curState: Int = text.sortDirection
            val newState: Int = getNewState(curState)
            drawStateTextView(newState, draws, res, text)
            Log.d(TAG, " The oldState is: $curState The new state is: $newState")
        }

        private fun drawStateTextView(
            newState: Int,
            draws: Array<Drawable?>?,
            res: Resources,
            text: StateTextView
        ) {
            when (newState) {
                StateTextView.SORT_DESC -> draws!![2] =
                    ResourcesCompat.getDrawable(res, R.drawable.ic_arrow_downward_24px, null)
                StateTextView.SORT_ASC -> draws!![2] =
                    ResourcesCompat.getDrawable(res, R.drawable.ic_arrow_upward_24px, null)
                else -> draws!![2] = null
            }
            Log.d(
                TAG, "The drawable to draw is: ............" + draws[2]
            )
            val bounds = Rect(0, 0, draws[2]?.intrinsicWidth ?: 0, draws[2]?.intrinsicHeight ?: 0)
            draws[2]?.bounds = bounds
            text.setCompoundDrawables(null, null, draws[2], null)
            text.sortDirection = newState

        }

        private fun getNewState(curState: Int): Int {
            return when (curState) {
                StateTextView.SORT_UNINITIALIZED -> StateTextView.SORT_DESC
                StateTextView.SORT_DESC -> StateTextView.SORT_ASC
                else -> StateTextView.SORT_DESC
            }
        }
    }
}