package com.example.alldocumentsreaderimagescanner.converter.model

import android.content.Context
import android.graphics.drawable.Drawable

class EnhancementOptionsEntity {
    var image: Drawable
    var name: String

    constructor(image: Drawable, name: String) {
        this.image = image
        this.name = name
    }

    constructor(context: Context, imageId: Int, name: String) {
        image = context.resources.getDrawable(imageId)
        this.name = name
    }

    constructor(context: Context, resourceId: Int, stringId: Int) {
        image = context.resources.getDrawable(resourceId)
        name = context.getString(stringId)
    }
}