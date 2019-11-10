package com.jere.android_sqlite_learning.customdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.jere.android_sqlite_learning.DataBaseHelper;
import com.jere.android_sqlite_learning.IGenerateBusinessCardListener;
import com.jere.android_sqlite_learning.R;
import com.jere.android_sqlite_learning.model.BusinessCard;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jere
 */
public class MyBusinessCardDialog implements View.OnClickListener {
    private static final String TAG = "MyBusinessCardDialog";
    private Context context;
    private AlertDialog alertDialog;
    private EditText nameEt;
    private EditText telephoneEt;
    private EditText addressEt;
    private DataBaseHelper dataBaseHelper;
    private boolean isUpdateCardInfo = false;
    private IGenerateBusinessCardListener mListener;
    private BusinessCard oldBusinessCard;
    private CheckBox maleCb;
    private CheckBox femaleCb;

    public MyBusinessCardDialog(Context context, boolean isUpdateCardInfo) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        this.isUpdateCardInfo = isUpdateCardInfo;
    }

    public void createDialogAndShow(BusinessCard businessCard, IGenerateBusinessCardListener listener) {
        this.mListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.add_business_card_dialog, null);
        maleCb = view.findViewById(R.id.male_cb);
        femaleCb = view.findViewById(R.id.female_cb);
        maleCb.setOnClickListener(this);
        femaleCb.setOnClickListener(this);
        nameEt = view.findViewById(R.id.name_et);
        telephoneEt = view.findViewById(R.id.telephone_et);
        addressEt = view.findViewById(R.id.address_et);
        if (businessCard != null) {
            oldBusinessCard = businessCard;
            nameEt.setText(businessCard.getName());
            telephoneEt.setText(businessCard.getTelephone());
            addressEt.setText(businessCard.getAddress());
        }
        Button yesBtn = view.findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(this);
        Button noBtn = view.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes_btn:
                BusinessCard newBusinessCard = generateNewBusinessCard();
                if (newBusinessCard == null) {
                    Toast.makeText(context, "pls select gender!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUpdateCardInfo) {
                    updateDatabaseBusinessCardInfo(newBusinessCard);
                } else {
                    insertBusinessCardDataToDatabase(newBusinessCard);
                }

                alertDialog.dismiss();
                break;
            case R.id.no_btn:
                alertDialog.dismiss();
                break;
            case R.id.male_cb:
                //sure client only select one checkbox, can't select maleCb and female together
                if (maleCb.isChecked()) {
                    femaleCb.setChecked(false);
                }
                break;
            case R.id.female_cb:
                //sure client only select one checkbox, can't select maleCb and female together
                if (femaleCb.isChecked()) {
                    maleCb.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    private BusinessCard generateNewBusinessCard() {
        BusinessCard businessCard = new BusinessCard();
        if (!maleCb.isChecked() && !femaleCb.isChecked()) {
            return null;
        } else if (maleCb.isChecked()) {
            businessCard.setGender(true);
        } else {
            businessCard.setGender(false);
        }
        businessCard.setName(nameEt.getText().toString());
        businessCard.setTelephone(telephoneEt.getText().toString());
        businessCard.setAddress(addressEt.getText().toString());
        businessCard.setAvatar(businessCard.getGender() ? R.drawable.male_avatar_icon : R.drawable.female_avatar_icon);

        return businessCard;
    }

    private void updateDatabaseBusinessCardInfo(BusinessCard businessCard) {
        dataBaseHelper.updateBusinessCard(oldBusinessCard.getId(), businessCard);
        mListener.getBusinessCard(businessCard);
    }

    private void insertBusinessCardDataToDatabase(BusinessCard businessCard) {
        dataBaseHelper.insertBusinessCard(businessCard);
        mListener.getBusinessCard(businessCard);
    }
}
