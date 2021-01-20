package co.ravn.kevin.peopleofstarwars.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import co.ravn.kevin.peopleofstarwars.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class LoadingComponent(context: Context, attrs: AttributeSet): LinearLayout(context, attrs)
{
    private var mCircularProgressBar: CircularProgressBar
    private var mLoadingTextView: TextView

    init {
        inflate(getContext(), R.layout.component_loading, this)

        mCircularProgressBar = findViewById(R.id.circularProgressBar)
        mLoadingTextView = findViewById(R.id.loadingTextView)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingComponent)
        mLoadingTextView.text = attributes.getString(R.styleable.LoadingComponent_text)
        attributes.recycle()

        hide()
    }

    fun show() {
        visibility = VISIBLE
        mCircularProgressBar.visibility = VISIBLE
        mLoadingTextView.visibility = VISIBLE
        mLoadingTextView.text = resources.getText(R.string.loading)
        mLoadingTextView.setTextColor(resources.getColor(R.color.black_50))
    }

    fun hide() {
        visibility = GONE
    }

    fun error(msg: String) {
        visibility = VISIBLE
        mCircularProgressBar.visibility = GONE
        mLoadingTextView.text = msg
        mLoadingTextView.setTextColor(resources.getColor(R.color.text_emphasis))
    }

}