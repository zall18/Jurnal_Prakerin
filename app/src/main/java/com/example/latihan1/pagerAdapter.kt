package com.example.latihan1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class pagerAdapter(var context: Context, var images: List<String>):PagerAdapter() {

    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var imageLoad = MainScope()

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = inflater.inflate(R.layout.imageitem, null)
        container.addView(view)

        var image = view.findViewById<ShapeableImageView>(R.id.image_slider)

        imageLoad.launch {
            var bitmap = getImageFromUrl(images[position])
            image.setImageBitmap(bitmap)
        }

        return view
    }

}