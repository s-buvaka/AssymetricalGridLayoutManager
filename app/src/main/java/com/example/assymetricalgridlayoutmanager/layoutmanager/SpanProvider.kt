package com.example.assymetricalgridlayoutmanager.layoutmanager

/**
 * @author s.buvaka
 */
interface SpanProvider {

    fun getSpanOnPosition(position: Int): SpanInfo
}
