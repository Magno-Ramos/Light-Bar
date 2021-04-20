package com.app.lightbar

import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class LightBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleRes) {

    private val lampView: LampView
    private var mSelectedView: View? = null

    init {
        View.inflate(context, R.layout.lightbar_view, this)
        lampView = findViewById(R.id.lamp)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        if (childCount > 1) child?.let(::setupView)
    }

    private fun setupView(view: View) {
        if (view.id == lampView.id) return

        if (view.isFirstMenuItem()) {
            ConstraintSet().apply {
                clone(this@LightBar)
                connect(view.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(
                    view.id,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                applyTo(this@LightBar)
            }

            mSelectedView = view
            moveLampToView(view, lampView)
        } else {
            view.darken()
        }

        view.setOnClickListener(::onClickItem)
    }

    private fun onClickItem(view: View) {
        TransitionManager.beginDelayedTransition(this@LightBar)
        moveLampToView(view, lampView)
        view.lighten()
        mSelectedView?.darken()
        mSelectedView = view
    }

    private fun moveLampToView(view: View, lampView: LampView) {
        ConstraintSet().apply {
            clone(this@LightBar)
            connect(lampView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(lampView.id, ConstraintSet.START, view.id, ConstraintSet.START)
            connect(lampView.id, ConstraintSet.END, view.id, ConstraintSet.END)
            applyTo(this@LightBar)
        }
    }

    private fun View.isFirstMenuItem(): Boolean {
        return indexOfChild(this) == 1
    }

    private fun View.darken() {
        this.alpha = 0.4f
    }

    private fun View.lighten() {
        this.alpha = 0.6f
    }
}