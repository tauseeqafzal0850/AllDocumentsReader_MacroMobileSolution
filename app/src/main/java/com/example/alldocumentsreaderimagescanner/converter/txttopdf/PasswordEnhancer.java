package com.example.alldocumentsreaderimagescanner.converter.txttopdf;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.alldocumentsreaderimagescanner.R;
import com.example.alldocumentsreaderimagescanner.converter.interfaces.Enhancer;
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity;
import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions;
import com.example.alldocumentsreaderimagescanner.converter.util.DialogUtils;
import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils;


/**
 * An {@link Enhancer} that lets you add and remove passwords
 */
public class PasswordEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;
    private final TextToPDFOptions.Builder mBuilder;

    PasswordEnhancer(@NonNull final Activity activity,
                     @NonNull final TextToPdfContract.View view,
                     @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mBuilder = builder;
        mBuilder.setPasswordProtected(false);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.ic_password_protect, R.string.set_password);
        mView = view;
    }

    @Override
    public void enhance() {
        MaterialDialog.Builder builder = DialogUtils.Companion.getInstance().createCustomDialogWithoutContent(mActivity,
                R.string.set_password);
        final MaterialDialog dialog = builder
                .customView(R.layout.custom_dialog, true)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);
        passwordInput.setText(mBuilder.getPassword());
        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

        positiveAction.setOnClickListener(v -> {
            if (StringUtils.Companion.getInstance().isEmpty(passwordInput.getText())) {
                StringUtils.Companion.getInstance().showInLayoutSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank,mActivity.findViewById(R.id.laySnackBar));
            } else {
                mBuilder.setPassword(passwordInput.getText().toString());
                mBuilder.setPasswordProtected(true);
                onPasswordAdded();
                dialog.dismiss();
            }
        });

        if (StringUtils.Companion.getInstance().isNotEmpty(mBuilder.getPassword())) {
            neutralAction.setOnClickListener(v -> {
                mBuilder.setPassword(null);
                onPasswordRemoved();
                mBuilder.setPasswordProtected(false);
                dialog.dismiss();
                StringUtils.Companion.getInstance().showInLayoutSnackbar(mActivity, R.string.password_remove,mActivity.findViewById(R.id.laySnackBar));

            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    private void onPasswordAdded() {
        mEnhancementOptionsEntity
                .setImage(mActivity.getResources().getDrawable(R.drawable.ic_password_protect));
        mView.updateView();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntity
                .setImage(mActivity.getResources().getDrawable(R.drawable.ic_password_protect));
        mView.updateView();
    }
}
