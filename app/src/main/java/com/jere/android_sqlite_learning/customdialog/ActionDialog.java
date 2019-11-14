package com.jere.android_sqlite_learning.customdialog;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.jere.android_sqlite_learning.DataBaseHelper;
import com.jere.android_sqlite_learning.IGenerateBusinessCardListener;
import com.jere.android_sqlite_learning.OperationTypeEnum;
import com.jere.android_sqlite_learning.R;
import com.jere.android_sqlite_learning.model.BusinessCard;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jere
 */
public class ActionDialog implements View.OnClickListener {
    private Context mContext;
    private AlertDialog mDialog;
    private IGenerateBusinessCardListener mListener;
    private BusinessCard businessCard;

    public ActionDialog(Context context) {
        mContext = context;
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
                new MyBusinessCardDialog(mContext,
                        OperationTypeEnum.UPDATE,
                        businessCard,
                        mListener);
                break;
            case R.id.delete_btn:
                mDialog.dismiss();
                DeleteBusinessCardAsyncTask deleteBusinessCardAsyncTask = new DeleteBusinessCardAsyncTask(mContext);
                deleteBusinessCardAsyncTask.execute(businessCard.getId());
                mListener.getBusinessCard(null);
                break;
            default:
                break;
        }
    }

    private static class DeleteBusinessCardAsyncTask extends AsyncTask<Integer, Void, Void> {

        private WeakReference<Context> contextWeakReference;

        DeleteBusinessCardAsyncTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            Context context = contextWeakReference.get();
            if (context != null) {
                new DataBaseHelper(context).deleteBusinessCard(integers[0]);
            }
            return null;
        }
    }
}
