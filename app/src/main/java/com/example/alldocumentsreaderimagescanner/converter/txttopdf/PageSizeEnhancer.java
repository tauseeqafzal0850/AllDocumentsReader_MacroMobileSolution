package com.example.alldocumentsreaderimagescanner.converter.txttopdf;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.alldocumentsreaderimagescanner.R;
import com.example.alldocumentsreaderimagescanner.converter.interfaces.Enhancer;
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity;
import com.example.alldocumentsreaderimagescanner.converter.util.PageSizeUtils;
import com.example.alldocumentsreaderimagescanner.converter.util.TextToPdfPreferences;


/**
 * An {@link Enhancer} that lets you select page size.
 */
public class PageSizeEnhancer implements Enhancer {

    private final PageSizeUtils mPageSizeUtils;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;

    PageSizeEnhancer(@NonNull final Context context) {
        mPageSizeUtils = new PageSizeUtils(context);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                context, R.drawable.ic_page_size, R.string.set_page_size_text);

        PageSizeUtils.mPageSize = new TextToPdfPreferences(context).getPageSize();
    }

    @Override
    public void enhance() {
        mPageSizeUtils.showPageSizeDialog(false);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

}
