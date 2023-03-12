package com.ideahamster.generalledger.data.util

import android.text.TextUtils
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        private const val TAG = "DateUtil"
        private val sourceDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH)
        private val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        private const val default_place_holder = "N/A"

        fun changeDateFormat(strDate: String?): String {
            if (TextUtils.isEmpty(strDate)) {
                return default_place_holder
            }
            return try {
                val date = sourceDateFormat.parse(strDate!!)
                if (date != null) displayDateFormat.format(date) else default_place_holder
            } catch (ex: ParseException) {
                ex.message?.let { Log.e(TAG, it) }
                default_place_holder
            }
        }

        fun toSourceDateString(displayDateString: String): String {
            if (TextUtils.isEmpty(displayDateString)) {
                return default_place_holder
            }
            return try {
                val date = displayDateFormat.parse(displayDateString)
                if (date != null) sourceDateFormat.format(date) else default_place_holder
            } catch (ex: ParseException) {
                ex.message?.let { Log.e(TAG, it) }
                default_place_holder
            }
        }
    }
}