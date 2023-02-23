package com.example.alldocumentsreaderimagescanner.converter.util;

import android.content.Context;

import com.example.alldocumentsreaderimagescanner.R;
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity;
import com.example.alldocumentsreaderimagescanner.converter.model.ImageToPDFOptions;

import java.util.ArrayList;


public class ImageEnhancementOptionsUtils {

    public ImageEnhancementOptionsUtils() {
    }


    private static class SingletonHolder {
        private static final ImageEnhancementOptionsUtils INSTANCE = new ImageEnhancementOptionsUtils();
    }

    public static ImageEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context, ImageToPDFOptions pdfOptions) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        int passwordIcon = R.drawable.ic_password_protect;
//        if (pdfOptions.isPasswordProtected())
//            passwordIcon = R.drawable.ic_baseline_done_24;

        options.add(new EnhancementOptionsEntity(
                context, passwordIcon, R.string.ic_password_protect));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_edit_image, R.string.crop_image));


        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_page_size, R.string.set_page_size_text));

//        options.add(new EnhancementOptionsEntity(
//                context, R.drawable.ic_page_size, R.string.set_scale_type));


        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_rearrange_icon, R.string.rearrange_images));


        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_margin_icon, R.string.add_margins));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_page_number_icon, R.string.show_pg_num));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_watermark_icon, R.string.add_watermark));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_page_color_icon, R.string.page_color));

        return options;
    }
}
