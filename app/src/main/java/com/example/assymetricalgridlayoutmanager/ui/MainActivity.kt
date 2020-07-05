package com.example.assymetricalgridlayoutmanager.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.assymetricalgridlayoutmanager.R
import com.example.assymetricalgridlayoutmanager.layoutmanager.AsymmetricGridLinearLayoutManager
import com.example.assymetricalgridlayoutmanager.layoutmanager.SpanInfo
import com.example.assymetricalgridlayoutmanager.layoutmanager.SpanProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
    }

    private fun initRecycler() {
        val layoutManager = AsymmetricGridLinearLayoutManager(this, spanCount = 5, spanProvider = object : SpanProvider {
            override fun getSpanOnPosition(position: Int): SpanInfo = SpanInfo(Random.nextInt(1, 6), Random.nextInt(1, 6))
        })
        layoutManager.isSquareCell = false
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.updateAdapter(getData())
    }

    private fun getData(): List<Int> = listOf(
        Color.YELLOW,
        Color.WHITE,
        Color.RED,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.GREEN,
        Color.GRAY,
        Color.DKGRAY,
        Color.CYAN,
        Color.BLUE,
        Color.YELLOW,
        Color.WHITE,
        Color.RED,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.GREEN,
        Color.GRAY,
        Color.DKGRAY,
        Color.CYAN,
        Color.BLUE,
        Color.YELLOW,
        Color.WHITE,
        Color.RED,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.GREEN,
        Color.GRAY,
        Color.DKGRAY,
        Color.CYAN,
        Color.BLUE
    )
}
