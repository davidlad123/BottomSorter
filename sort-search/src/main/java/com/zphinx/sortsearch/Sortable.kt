package com.zphinx.sortsearch

import java.util.*

/**
 * An interface used to define additional methods for the comparable interface
 * Created by rogue on 07/09/15.
 */
interface Sortable<T> : Comparable<T> {
    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @param index   An index used to specify the property which will determine the comparator
     * @return a negative integer if this instance is less than `another`;
     * a positive integer if this instance is greater than
     * `another`; 0 if this instance has the same order as
     * `another`.
     * @throws ClassCastException if `another` cannot be converted into something
     * comparable to `this` instance.
     */
    fun compareTo(another: T?, index: Int): Int

    fun compareInts(thisIntProperty: Int, thatIntProperty: Int): Int {
        return when {
            thisIntProperty > thatIntProperty -> 1
            thisIntProperty < thatIntProperty -> -1
            else -> 0
        }
    }


    fun compareStrings(thisString: String?, thatString: String?): Int {
        when {
            thisString == null && thatString == null -> {
                return 0
            }
            else -> {
                thisString?.let { curString ->
                    thatString?.let {
                        return curString.compareTo(it, true)
                    } ?: return 1
                } ?: return -1
            }
        }

    }

    fun compareDates(currentJobDate: Date?, anotherJobDate: Date?): Int {
        when {
            currentJobDate == null && anotherJobDate == null -> {
                return 0
            }
            else -> {
                currentJobDate?.let { thisJobDate ->
                    anotherJobDate?.let {
                        return thisJobDate.compareTo(it)
                    } ?: return 1
                } ?: return -1
            }
        }

    }


}