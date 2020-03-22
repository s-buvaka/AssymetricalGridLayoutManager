package com.example.assymetricalgridlayoutmanager.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assymetricalgridlayoutmanager.R
import com.example.assymetricalgridlayoutmanager.layoutmanager.AsymmetricGridLinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
    }

    private fun initRecycler() {
        recyclerView.layoutManager = AsymmetricGridLinearLayoutManager()
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
