package com.zphinx.sortattributes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.LayoutInflater.from
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zphinx.sortsearch.SortProperty

import java.util.*

class MainActivity : AppCompatActivity(), SortProperty {

    /**
     * Determines if we are sorting ascending or sorting descending
     */
    override var sortDirection = 0
    /**
     * Defines what property to use to execute the sort
     */
    override var sortIndex = 0
    private var mList: ListView? = null
    private var mAdapter: SortListAdapter? = null
    private var mSortButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_view)
        mList = findViewById<ListView>(android.R.id.list)
        initButton()
        mSortables = createSortables()
        mAdapter =
            SortListAdapter(this@MainActivity, R.layout.list_item, R.id.user_name, mSortables)
        Log.e(TAG, "the adapter has item size: " + mAdapter!!.count)
        mList?.adapter = mAdapter
    }

    private fun initButton() {
        mSortButton = findViewById<Button>(R.id.btnSortSearch)
        mSortButton?.setOnClickListener {
            val sm = com.zphinx.sortsearch.SortDialogManager()
            sm.showAlertDialog(this@MainActivity) { fireSorter() }

        }
    }

    /**
     * Gets an array ofsortables for use by this activity
     *
     * @return An instance of an array list
     */
    private fun createSortables(): ArrayList<com.zphinx.sortsearch.SortableImpl<Any>?>? {
        val allSorts: ArrayList<com.zphinx.sortsearch.SortableImpl<Any>?> = ArrayList(LIST_NUM)
        val difference: Long = 3600000
        for (i in 0 until LIST_NUM) {
            allSorts.add(createSingleSort(i, difference))
        }
        return allSorts
    }

    /**
     * Creates a sortable object using the parameters given
     *
     * @param index
     * The index to use for generating values
     * @param difference
     * The time difference to specify between values
     */
    private fun createSingleSort(
        index: Int,
        difference: Long
    ): com.zphinx.sortsearch.SortableImpl<Any>? {
        val sort = com.zphinx.sortsearch.SortableImpl<Any>()
        val sysTime: Long = System.currentTimeMillis()
        val time = sysTime + difference * randInt(1, 20)
        val modDate = sysTime + difference * randInt(1, 10)
        sort.numberOfViews = randInt(0, 100)
        sort.modificationDate = Date(modDate)
        sort.lastLoginDate = Date(time)
        sort.price = randDouble()
        sort.userName = "User $index"
        return sort
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * `Integer.MAX_VALUE - 1`.
     *
     * @param min
     * Minimum value
     * @param max
     * Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random.nextInt
     */
    private fun randInt(
        min: Int,
        max: Int
    ): Int { // Usually this can be a field rather than a method variable
        val rand = Random()
        // nextInt is normally exclusive of the top value,
// so add 1 to make it inclusive
        return rand.nextInt(max - min + 1) + min
    }

    /**
     * Generates a random double for use by this app
     *
     * @return A random double number
     */
    private fun randDouble(): Double { // Usually this can be a field rather than a method variable
        val rand = Random()
        // nextInt is normally exclusive of the top value,
// so add 1 to make it inclusive
        val randomNum: Double = rand.nextDouble() * 100
        return Math.round(randomNum).toDouble()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item!!.itemId
        // noinspection SimplifiableIfStatement
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    /**
     * Sorts the list of object based on the sortIndex and the SortDirection
     */
    private fun fireSorter() {
        Log.d(TAG,"Fireing sort algorithms...")
        Collections.sort(mSortables, SortComparator(sortIndex))
        if (sortDirection == com.zphinx.sortsearch.StateTextView.SORT_ASC) {
            Collections.reverse(mSortables)
        }
        mAdapter?.setOptions(mSortables)
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * The list adapter used by the associated list view
     */
    inner class SortListAdapter(
        context: Context?,
        resourceId: Int,
        textResourceId: Int,
        options: MutableList<com.zphinx.sortsearch.SortableImpl<Any>?>?
    ) : ArrayAdapter<com.zphinx.sortsearch.SortableImpl<Any>?>(
        context!!,
        resourceId,
        textResourceId,
        options!!
    ) {
        private var options: MutableList<com.zphinx.sortsearch.SortableImpl<Any>?>?
        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getItem(int)
         */
        override fun getItem(position: Int): com.zphinx.sortsearch.SortableImpl<Any>? {
            return if (!isEmpty && position > -1) {
                options?.get(position)
            } else null
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position
         * The position of the item within the adapter's data set
         * whose row id we want.
         * @return The id of the item at the specified position.
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * {@inheritDoc}
         */
        override fun getCount(): Int {
            return options!!.size
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater: LayoutInflater = from(this@MainActivity)
            val rowView: View = inflater.inflate(R.layout.list_item, parent, false)
            val sorted: com.zphinx.sortsearch.SortableImpl<Any> = options?.get(position)!!
            val modDate: TextView = rowView.findViewById(R.id.mod_date) as TextView
            modDate.text = sorted.modificationDate.toString()
            val lastLogin: TextView = rowView.findViewById(R.id.last_login) as TextView
            lastLogin.text = sorted.lastLoginDate.toString()
            val userName: TextView = rowView.findViewById(R.id.user_name) as TextView
            userName.text = sorted.userName
            val numViews: TextView = rowView.findViewById(R.id.num_views) as TextView
            numViews.text = "Views:  ${sorted.numberOfViews}"
            val price: TextView = rowView.findViewById(R.id.price) as TextView
            price.text = "Price: $Double"
            Log.d(
                TAG,
                "setting up list index $position with user name:  ${userName.text}"
            )
            return rowView
        }

        fun setOptions(options: MutableList<com.zphinx.sortsearch.SortableImpl<Any>?>?) {
            this.options = options
        }

        init {
            this.options = options
            Log.d(TAG, "The options size is: " + options?.size)
        }
    }

    /**
     * A comparator used during the sorting process
     *
     */
    inner class SortComparator (private val index: Int) : Comparator<com.zphinx.sortsearch.SortableImpl<Any>?> {

        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        override fun compare(
            lhs: com.zphinx.sortsearch.SortableImpl<Any>?,
            rhs: com.zphinx.sortsearch.SortableImpl<Any>?
        ): Int {
            return lhs!!.compareTo(rhs as Any, index)
        }

    }

    companion object {
        const val LIST_NUM = 10

        private val TAG: String? = MainActivity::class.java.simpleName
        private var mSortables: MutableList<com.zphinx.sortsearch.SortableImpl<Any>?>? = null
    }
}