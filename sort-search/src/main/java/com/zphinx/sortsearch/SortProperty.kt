package com.zphinx.sortsearch


/**
 * The interface which defines what properties are associated with a sortable collection should have
 * @author David Ladapo
 *
 * Created  on 23 Jan 2020 10:31
 * @copyright City Holdings LTD 2019
 *
 */
interface SortProperty {
    var sortIndex: Int
    var sortDirection: Int

    fun processSorting(index: Int, direction: Int, fireSorter: () -> Unit) {
        sortIndex = index
        sortDirection = direction
        fireSorter.invoke()
    }

}

