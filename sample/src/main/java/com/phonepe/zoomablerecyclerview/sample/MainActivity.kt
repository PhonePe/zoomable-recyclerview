package com.phonepe.zoomablerecyclerview.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.phonepe.zoomablerecyclerview.ZoomableRecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<ZoomableRecyclerView>(R.id.zoomableRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = SampleAdapter(generateItems())
    }

    private fun generateItems(): List<SampleItem> {
        val colors = listOf(
            Color.parseColor("#EF5350"), // Red
            Color.parseColor("#EC407A"), // Pink
            Color.parseColor("#AB47BC"), // Purple
            Color.parseColor("#5C6BC0"), // Indigo
            Color.parseColor("#42A5F5"), // Blue
            Color.parseColor("#26C6DA"), // Cyan
            Color.parseColor("#26A69A"), // Teal
            Color.parseColor("#66BB6A"), // Green
            Color.parseColor("#D4E157"), // Lime
            Color.parseColor("#FFCA28"), // Amber
            Color.parseColor("#FFA726"), // Orange
            Color.parseColor("#FF7043"), // Deep Orange
            Color.parseColor("#8D6E63"), // Brown
            Color.parseColor("#78909C"), // Blue Grey
            Color.parseColor("#42A5F5"), // Blue
        )
        val labels = listOf(
            "Landscape", "Portrait", "Macro", "Street", "Nature",
            "Architecture", "Abstract", "Wildlife", "Travel", "Food",
            "Fashion", "Sports", "Night", "Aerial", "Underwater"
        )
        return (1..30).map { i ->
            SampleItem(
                number = i,
                color = colors[(i - 1) % colors.size],
                label = labels[(i - 1) % labels.size]
            )
        }
    }
}
