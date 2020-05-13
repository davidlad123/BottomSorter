package com.zphinx.sortsearch

import java.util.*

/**
 * An implementation of the sortable interface used to by this activity.
 * Sortable defines a way for a collection of objects to be sorted using a
 * specified index. Created by rogue on 07/09/15.
 */
class SortableImpl<Any> : com.zphinx.sortsearch.Sortable<Any> {
    var modificationDate: Date? = null
    var userName: String? = null
    var lastLoginDate: Date? = null
    var price: Double = 0.0
    var numberOfViews = 0
    var currentSortIndex = 0
    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param other
     * the object to compare to this instance.
     * @return a negative integer if this instance is less than `another`;
     * a positive integer if this instance is greater than
     * `another`; 0 if this instance has the same order as
     * `another`.
     * @throws ClassCastException
     * if `another` cannot be converted into something
     * comparable to `this` instance.
     */
    override fun compareTo(other: Any): Int {
        return compareTo(other, currentSortIndex)
    }

    /**
     * Compares this object to the specified object using the index passed to it
     * in order to determine their relative order.
     *
     * @param another
     * the object to compare to this instance.
     * @param index
     * An index used to specify the property which will determine the
     * comparator
     * @return a negative integer if this instance is less than `another`;
     * a positive integer if this instance is greater than
     * `another`; 0 if this instance has the same order as
     * `another`.
     * @throws ClassCastException
     * if `another` cannot be converted into something
     * comparable to `this` instance.
     */
    override fun compareTo(another: Any?, index: Int): Int {
        var compareValue = 0
        if (another !is SortableImpl<*>) {
            throw RuntimeException("Unable to process objects which are not sortable instances")
        } else {
            val sortable = another as SortableImpl<*>?
            when (index) {
                SORT_BY_MODIFICATION -> {
                    compareValue = modificationDate?.compareTo(sortable!!.modificationDate) ?: -1
                }
                SORT_BY_LOGIN -> {
                    compareValue = lastLoginDate?.compareTo(sortable!!.lastLoginDate) ?: -1
                }
                SORT_BY_NAME -> compareValue = userName!!.compareTo(sortable!!.userName!!)
                SORT_BY_VIEWS -> compareValue =
                    if (numberOfViews == sortable!!.numberOfViews) 0 else if (numberOfViews > sortable.numberOfViews) 1 else -1
                SORT_BY_PRICE -> compareValue = price.compareTo(sortable!!.price)
                else -> {
                }
            }
        }
        return compareValue
    }

    override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (other !is SortableImpl<*>) return false


        if (modificationDate != other.modificationDate) return false
        if (userName != other.userName) return false
        if (lastLoginDate != other.lastLoginDate) return false
        if (price != other.price) return false
        if (numberOfViews != other.numberOfViews) return false
        if (currentSortIndex != other.currentSortIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modificationDate?.hashCode() ?: 0
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + (lastLoginDate?.hashCode() ?: 0)
        result = 31 * result + price.hashCode()
        result = 31 * result + numberOfViews
        result = 31 * result + currentSortIndex
        return result
    }

    companion object {
        const val SORT_BY_MODIFICATION = 1
        const val SORT_BY_LOGIN = 2
        const val SORT_BY_NAME = 3
        const val SORT_BY_VIEWS = 4
        const val SORT_BY_PRICE = 5
    }


}