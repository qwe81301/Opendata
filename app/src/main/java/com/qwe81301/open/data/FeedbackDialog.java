package com.qwe81301.open.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.qwe81301.open.data.interfaceutil.OnSignDialog3ResultListener;

/**
 * author:       bearshih
 * project:      OpenData
 * date:         2019/9/23
 * version:
 * description: 意見回報
 */
public class FeedbackDialog {

    //(目前測試)test 分支 單commit 合併(2-2)
    //(目前測試)test 分支 單commit 合併(3-2)

    private Context mContext;
    private Activity mActivity;

    private EditText mNoteEditText;//簽核意見

    public FeedbackDialog(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    public void showDialog(final OnSignDialog3ResultListener onDialogListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

//        builder.setTitle("選擇請假開始時間");

        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.dialog_feed_back, null, false);

        mNoteEditText = view.findViewById(R.id.editText_reason);

        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String note = mNoteEditText.getText().toString();

                onDialogListener.dialogPositiveResult(note);
            }
        });

        builder.setNegativeButton("退回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String note = mNoteEditText.getText().toString();

                onDialogListener.dialogNegativeResult(note);
            }
        });

        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //取消不做事 (但是還是留著這介面以防以後需要用到)
                onDialogListener.dialogNeutralResult();
            }
        });

        //todo(提醒) builder.setView(view) 和 final AlertDialog dialog 和 dialog.show() 宣告順序先後有差 不對會出錯
        // set dialog view
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        // show it to user
        dialog.show();

        //退回文字 改成紅字 以表提醒
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.red));
    }
}
