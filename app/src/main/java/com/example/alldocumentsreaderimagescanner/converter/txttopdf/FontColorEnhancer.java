package com.example.alldocumentsreaderimagescanner.converter.txttopdf;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.alldocumentsreaderimagescanner.R;
import com.example.alldocumentsreaderimagescanner.converter.interfaces.Enhancer;
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity;
import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions;
import com.example.alldocumentsreaderimagescanner.converter.util.ColorUtils;
import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils;
import com.example.alldocumentsreaderimagescanner.converter.util.TextToPdfPreferences;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;


/**
 * An {@link Enhancer} that lets you select font colors.
 */
public class FontColorEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    FontColorEnhancer(@NonNull final Activity activity,
                      @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.ic_font_color, R.string.font_color);
    }

    @Override
    public void enhance() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.font_color)
                .customView(R.layout.dialog_color_chooser, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
                    final int fontcolor = colorPickerView.getColor();
                    final int pageColor = mPreferences.getPageColor();
                    if (ColorUtils.Companion.getInstance().colorSimilarCheck(fontcolor, pageColor)) {
                        StringUtils.Companion.getInstance().showInLayoutSnackbar(mActivity, R.string.snackbar_color_too_close,mActivity.findViewById(R.id.laySnackBar));
                    }
                    if (defaultCheckbox.isChecked()) {
                        mPreferences.setFontColor(fontcolor);
                    }
                    mBuilder.setFontColor(fontcolor);
                })
                .build();
        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mBuilder.getFontColor());
        materialDialog.show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}
