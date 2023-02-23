package com.example.alldocumentsreaderimagescanner.converter.txttopdf

import android.app.Activity
import com.example.alldocumentsreaderimagescanner.converter.interfaces.Enhancer
import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions

/**
 * The [Enhancers] represent a list of enhancers for the Text-to-PDF feature.
 */
enum class Enhancers {
    FONT_COLOR {
        override fun getEnhancer(
            activity: Activity?, view: TextToPdfContract.View?,
            builder: TextToPDFOptions.Builder?
        ): Enhancer {
            return FontColorEnhancer(activity!!, builder!!)
        }
    },
    FONT_SIZE {
        override fun getEnhancer(
            activity: Activity?, view: TextToPdfContract.View?,
            builder: TextToPDFOptions.Builder?
        ): Enhancer {
            return FontSizeEnhancer(activity!!, view!!, builder!!)
        }
    },
    PAGE_COLOR {
        override fun getEnhancer(
            activity: Activity?, view: TextToPdfContract.View?,
            builder: TextToPDFOptions.Builder?
        ): Enhancer {
            return PageColorEnhancer(
                activity!!,
                builder!!
            )
        }
    },
    PAGE_SIZE {
        override fun getEnhancer(
            activity: Activity?, view: TextToPdfContract.View?,
            builder: TextToPDFOptions.Builder?
        ): Enhancer {
            return PageSizeEnhancer(activity!!)
        }
    },
    PASSWORD {
        override fun getEnhancer(
            activity: Activity?, view: TextToPdfContract.View?,
            builder: TextToPDFOptions.Builder?
        ): Enhancer {
            return PasswordEnhancer(activity!!, view!!, builder!!)
        }
    };

    /**
     * @param activity The [Activity] context.
     * @param view The [TextToPdfContract.View] that needs the enhancement.
     * @param builder The builder for [TextToPDFOptions].
     * @return An instance of the [Enhancer].
     */
    abstract fun getEnhancer(
        activity: Activity?, view: TextToPdfContract.View?,
        builder: TextToPDFOptions.Builder?
    ): Enhancer
}