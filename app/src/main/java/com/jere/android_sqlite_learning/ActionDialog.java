package com.jere.android_sqlite_learning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jere
 */
public class ActionDialog implements View.OnClickListener {
    private Context mContext;
    private int mPosition;
    private DataBaseHelper dataBaseHelper;
    private AlertDialog mDialog;
    private IGenerateBusinessCardListener mListener;
    private BusinessCard businessCard;

    ActionDialog(Context context, int position) {
        mContext = context;
        mPosition = position;
        dataBaseHelper = new DataBaseHelper(context);
    }

    public void createDialogAndShow(BusinessCard businessCard, IGenerateBusinessCardListener listener) {
        this.mListener = listener;
        this.businessCard = businessCard;
        View actionDialog = LayoutInflater.from(mContext).inflate(R.layout.action_dialog, null);
        Button editBtn = actionDialog.findViewById(R.id.edit_btn);
        Button deleteBtn = actionDialog.findViewById(R.id.delete_btn);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(actionDialog);
        mDialog = builder.create();
        mDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn:
                mDialog.dismiss();
                MyBusinessCardDialog myBusinessCardDialog = new MyBusinessCardDialog(mContext, true);
                myBusinessCardDialog.createDialogAndShow(businessCard, mListener);
                break;
            case R.id.delete_btn:
                mDialog.dismiss();
                int id = dataBaseHelper.getBusinessCardId(businessCard);
                dataBaseHelper.deleteBusinessCard(id);
                mListener.getBusinessCard(null);
                break;
            default:
                break;
        }
    }
}
