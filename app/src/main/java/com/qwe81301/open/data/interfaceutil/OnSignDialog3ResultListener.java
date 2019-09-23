package com.qwe81301.open.data.interfaceutil;

/**
 * author:       bearshih
 * project:      EHRMS
 * date:         2019/9/9
 * version:
 * description: Dialog 包含三個回傳的實做
 */
public interface OnSignDialog3ResultListener {
    //同意 和 退回 都只需要回傳 核覆意見
    void dialogPositiveResult(String note);

    void dialogNegativeResult(String note);

    //取消 不需要回傳核覆意見 (但是還是留著這介面以防以後需要用到)
    void dialogNeutralResult();
}
