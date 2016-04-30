package com.picker.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import come.picker.R;
import com.picker.utils.FontCache;


public class TextView extends android.support.v7.widget.AppCompatTextView {

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO remove if don't want custom font
        init(context, attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //TODO remove if don't want custom font
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            if (attrs != null) {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Font);
                int textStyle = typedArray.getInt(R.styleable.Font_textStyle, 0);
                switch (textStyle) {
                    case 0:
                        setTypeface(FontCache.getTypeface(context.getString(R.string.avenir_regular), context));
                        break;
                    case 1:
                        setTypeface(FontCache.getTypeface(context.getString(R.string.avenir_medium), context));
                        break;
                    case 2:
                        setTypeface(FontCache.getTypeface(context.getString(R.string.avenir_bold), context));
                        break;
                    default:
                        setTypeface(FontCache.getTypeface(context.getString(R.string.avenir_regular), context));
                        break;
                }
                typedArray.recycle();
            }
        }
    }

}