package com.minorfish.car.twoth.extend;

import android.util.TypedValue;
import android.view.View;

public class ViewExt {

    public static void rippleRect(View view) {
        if(view == null) return;

        TypedValue typedValue = new TypedValue();
        if(view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
            view.setBackgroundResource(typedValue.resourceId);
        }
    }

    public static void rippleOval(View view) {
        if(view == null) return;

        TypedValue typedValue = new TypedValue();
        if(view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)) {
            view.setBackgroundResource(typedValue.resourceId);
        }
    }
}
