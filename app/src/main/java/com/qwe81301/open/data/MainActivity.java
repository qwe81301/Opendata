package com.qwe81301.open.data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qwe81301.open.data.util.RecyclerViewNoBugLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private OkHttpProxy mOkHttpProxy;
    private String responseStr = "";

    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;


//    List<BikeRentDataBean> weekDateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOkHttpProxy = new OkHttpProxy(getApplicationContext(), this);

        //RecyclerView的初始化
        mRecyclerView = findViewById(R.id.recyclerView_bike_record);

        setRecyclerView();

        //請求政府 Open Data
        requestOpenData();


    }


    private void setRecyclerView() {

        //設置LayoutManager
        mRecyclerView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getApplicationContext()));
        //設置item的动画，可以不設置
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        if (getContext() != null) {
        //設置item的分隔線
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
//        }
    }

    /**
     * 請求政府 Open Data
     */
    private void requestOpenData() {
        service.submit(new Runnable() {//  一定要加這個才能跟伺服器連
            @Override
            public void run() {

//                ActDataBean actData = new ActDataBean();
//                actData.setMethod("sendToSign");//Method:"1" = 將要發送的資料寫入排程
//
//                TokenRequestDataBean tokenRequestData = new TokenRequestDataBean();
//                tokenRequestData.setToken(ToolsHelper.readTokenData());
//
//                FCMToNotificationSignerRequestDataBean FCMToNotificationSignerRequestData = new FCMToNotificationSignerRequestDataBean();
//                FCMToNotificationSignerRequestData.setSNo(tsNo);
//                FCMToNotificationSignerRequestData.setSecret(tSecret);
//                FCMToNotificationSignerRequestData.setData("");

//                Gson gson = new Gson();
//                String actDataTransferToApiJson = gson.toJson(actData);
//                String tokenRequestDataTransferToApiJson = gson.toJson(tokenRequestData);
//                String normalDataTransferToApiJson = gson.toJson(FCMToNotificationSignerRequestData);

//                responseStr = mOkHttpProxy.post("", "actRaw", "tokenRaw",
//                        actDataTransferToApiJson, tokenRequestDataTransferToApiJson);

                responseStr = mOkHttpProxy.get("https://www.easytraffic.com.tw/OpenService/Bike/BikeRentData?$top=20");

                //現在寫法Network is unstable(網路不穩)和Unpredictable error(不在預期的錯誤)都是以下情況
                if ("Network is unstable".equals(responseStr)) {
                    //網路不穩(關閉wifi 後 右打開 wifi 並迅速點擊打卡按鈕)
//                    ToolsHelper.showSimplePromptDialog(mContext, getActivity(), "確認", "沒有網路", "請確認網路狀態");

                    //無法預期的錯誤Unpredictable error 現在暫訂認定為 連線逾時造成連線失敗 伺服器無回應
                } else if ("Connect timed out".equals(responseStr)
                        || "Unpredictable error".equals(responseStr)) {
                    //連線逾時造成連線失敗 伺服器無回應 (只開行動網路(沒用wifi) 點擊打卡按鈕)
//                    ToolsHelper.showSimplePromptDialog(getContext(), getActivity(), "確認", "連線錯誤", "伺服器無回應");

                } else {
                    try {

                        Log.v("TEST", "responseStr:" + responseStr);

                        //todo 如果資料不是每次都能順利拿到 到時候放放在 最下面 用死的資料
                        Gson gson = new Gson();
//                        BikeRentDataBean bikeRentDataBeanForList = gson.fromJson(responseDataStr, BikeRentDataBean.class);
                        List<BikeRentDataBean> bikeRentDataBeanForList = gson.fromJson(responseStr, new TypeToken<List<BikeRentDataBean>>() {
                        }.getType());

                        ArrayList<BikeRentDataBean> bikeRentDataList = new ArrayList<>();
//
//                        字符串为为list
//                        List<Person> persons =gson.fromJson(json, new TypeToken<List<Person>>() {}.getType());

                        for (int i = 0; i < bikeRentDataBeanForList.size(); i++) {
                            BikeRentDataBean bikeRentDataBean = new BikeRentDataBean();
                            bikeRentDataBean.setStop_id(bikeRentDataBeanForList.get(i).getStop_id());
                            bikeRentDataBean.setName(bikeRentDataBeanForList.get(i).getName());

                            bikeRentDataBean.setSumSpace(bikeRentDataBeanForList.get(i).getSumSpace());
                            bikeRentDataBean.setVehicles(bikeRentDataBeanForList.get(i).getVehicles());
                            bikeRentDataBean.setParkingSpaces(bikeRentDataBeanForList.get(i).getParkingSpaces());
                            bikeRentDataList.add(bikeRentDataBean);
                        }
                        Log.v("TEST", bikeRentDataList.toString());

                        mAdapter = new MyAdapter(
                                bikeRentDataList
                        );

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


//    StopID	String	站點代號	0112
//    Name	String	場站名稱(中)	捷運新北投站
//    NameEn	String	場站名稱(英)	MRT Xinbeitou Sta.
//    Lon	float	經度	121.50312042236328
//    Lat	float	緯度	25.1374568939209
//    Address	String	地址(中)	大業路/中和街交叉口
//    AddressEn	String	地址(英)	Daye Rd. & Zhonghe St. Intersection
//    CityID	String	縣市ID	02
//    Area	String	場站區域(中)	北投區
//    AreaEn	String	場站區域(英)	Beitou Dist.
//    SumSpace	int	場站總停車格	48
//    Vehicles	int	可借車位數	29
//    ParkingSpaces	int	可還空位數	17
//    IsService	int	場站是否營運	1	1:是 0:否
//    UpdateTime	String	更新時間	2016-03-16T10:13:48.677

    private void dataList() {

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<BikeRentDataBean> bikeRentItems;

        public MyAdapter(List<BikeRentDataBean> bikeRentItems) {

            this.bikeRentItems = bikeRentItems;
        }

        /**
         * 创建ViewHolder的布局
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_bike_record, parent, false);
            return new ViewHolder(view);
        }

        /**
         * 通过ViewHolder将数据绑定到界面上进行显示
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

//            bikeRentItems.get(position).getStop_id()
            holder.mStopIdTextView.setText(bikeRentItems.get(position).getStop_id());
            holder.mNameTextView.setText(bikeRentItems.get(position).getName());

            holder.mCanUseBikeNumberTextView.setText("可借：" + bikeRentItems.get(position).getVehicles());
            holder.mCanDropOffBikeNumberTextView.setText("可還：" + bikeRentItems.get(position).getParkingSpaces());

            double sumSpace = (double) bikeRentItems.get(position).getSumSpace();
            double vehicles = (double) bikeRentItems.get(position).getVehicles();

            //計算 比例
            double persentCalcurlate = (vehicles / sumSpace);

            //1 ~ 0.4    0.4 ~ 0
            if (1 >= persentCalcurlate && persentCalcurlate > 0.4) {
                holder.mStatusTextView.setText("車輛充足");
                //todo(提示用)修改提示字背景
                holder.mStatusTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_border));
            } else if (0.4 >= persentCalcurlate && persentCalcurlate > 0) {
                holder.mStatusTextView.setText("車輛稍少");
                holder.mStatusTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_border));
            } else if (persentCalcurlate == 0) {
                holder.mStatusTextView.setText("無車可取");
                holder.mStatusTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_border));
            }
//
//
//            //合併 X月X日(週幾)
//            String dateStr = mCurrentMonth + "月" + dayDateItems.get(position) + "日" + "(" + weekDateItems.get(position) + ")";
//            holder.mDateTextView.setText(dateStr);
//
////            if (applyCountItems.get(position) > 0) {
////                holder.mOvertimeSurveyClickAreaConstraintLayout.setVisibility(View.VISIBLE);
////            } else {
////                holder.mOvertimeSurveyClickAreaConstraintLayout.setVisibility(View.INVISIBLE);
////            }
//
//
//            if (!"".equals(toWorkStr0Items.get(position)) && toWorkStr0Items.get(position) != null) {
//                holder.mToWorkStr0TextView.setText(toWorkStr0Items.get(position));
//                holder.mToWorkStr0TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mToWorkStr0TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mToWorkStr0TextView.setVisibility(View.INVISIBLE);
//            }
//
//            if (!"".equals(toWorkStr1Items.get(position)) && toWorkStr1Items.get(position) != null) {
//                holder.mToWorkStr1TextView.setText(toWorkStr1Items.get(position));
//                holder.mToWorkStr1TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mToWorkStr1TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mToWorkStr1TextView.setVisibility(View.INVISIBLE);
//            }
//
//            if (!"".equals(toWorkStr2Items.get(position)) && toWorkStr2Items.get(position) != null) {
//                holder.mToWorkStr2TextView.setText(toWorkStr2Items.get(position));
//                holder.mToWorkStr2TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mToWorkStr2TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mToWorkStr2TextView.setVisibility(View.INVISIBLE);
//            }
//
//
//            if (!"".equals(offWorkStr0Items.get(position)) && offWorkStr0Items.get(position) != null) {
//                holder.mOffWorkStr0TextView.setText(offWorkStr0Items.get(position));
//                holder.mOffWorkStr0TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mOffWorkStr0TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mOffWorkStr0TextView.setVisibility(View.INVISIBLE);
//            }
//
//            if (!"".equals(offWorkStr1Items.get(position)) && offWorkStr1Items.get(position) != null) {
//                holder.mOffWorkStr1TextView.setText(offWorkStr1Items.get(position));
//                holder.mOffWorkStr1TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mOffWorkStr1TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mOffWorkStr1TextView.setVisibility(View.INVISIBLE);
//            }
//
//            if (!"".equals(offWorkStr2Items.get(position)) && offWorkStr2Items.get(position) != null) {
//                holder.mOffWorkStr2TextView.setText(offWorkStr2Items.get(position));
//                holder.mOffWorkStr2TextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.mOffWorkStr2TextView.setText("00:00");//這邊沒有值的話 因為排版關係 還是給"00:00" 但是改成invisible 即可
//                holder.mOffWorkStr2TextView.setVisibility(View.INVISIBLE);
//            }
//
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(twkindStr0Items.get(position))) {
//                holder.mTwKindStr0TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mTwKindStr0TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(twkindStr1Items.get(position))) {
//                holder.mTwKindStr1TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mTwKindStr1TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(twkindStr2Items.get(position))) {
//                holder.mTwKindStr2TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mTwKindStr2TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//            holder.mTwKindStr0TextView.setText(twkindStr0Items.get(position));
//            holder.mTwKindStr1TextView.setText(twkindStr1Items.get(position));
//            holder.mTwKindStr2TextView.setText(twkindStr2Items.get(position));
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(owkindStr0Items.get(position))) {
//                holder.mOwKindStr0TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mOwKindStr0TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(owkindStr1Items.get(position))) {
//                holder.mOwKindStr1TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mOwKindStr1TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//
//            //打卡正常為黑色 打卡異常為紅色
//            if ("正常".equals(owkindStr2Items.get(position))) {
//                holder.mOwKindStr2TextView.setTextColor(getResources().getColor(R.color.black));
//            } else {
//                holder.mOwKindStr2TextView.setTextColor(getResources().getColor(R.color.red));
//            }
//            holder.mOwKindStr0TextView.setText(owkindStr0Items.get(position));
//            holder.mOwKindStr1TextView.setText(owkindStr1Items.get(position));
//            holder.mOwKindStr2TextView.setText(owkindStr2Items.get(position));
//
//
//            //otSurvey0,1,2 == 1 的時候 才顯示超時調查按鈕
//            //先隱藏 mOvertimeSurvey0ImageView 和 mOvertimeSurvey0TextView
//            holder.mOvertimeSurvey0ImageView.setVisibility(View.GONE);
//            holder.mOvertimeSurvey0TextView.setVisibility(View.GONE);
//            switch (otSurvey0Items.get(position)) {
//                case 1:
//                    holder.mOvertimeSurvey0ImageView.setVisibility(View.VISIBLE);
//                    holder.mOvertimeSurvey0TextView.setVisibility(View.GONE);
//                    break;
//                case 2:
//                    holder.mOvertimeSurvey0ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey0TextView.setText("　加班　");//比照最長的 "加班(代)" 所以後來調整　前後各加一個全型空白
//                    holder.mOvertimeSurvey0TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey0TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 3:
//                    holder.mOvertimeSurvey0ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey0TextView.setText("　私務　");
//                    holder.mOvertimeSurvey0TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey0TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 4:
//                    holder.mOvertimeSurvey0ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey0TextView.setText("加班(代)");
//                    holder.mOvertimeSurvey0TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey0TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 5:
//                    holder.mOvertimeSurvey0ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey0TextView.setText("私務(代)");
//                    holder.mOvertimeSurvey0TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey0TextView.setVisibility(View.VISIBLE);
//                    break;
//                default:
//                    break;
//            }
//
//            //先隱藏 mOvertimeSurvey1ImageView 和 mOvertimeSurvey1TextView
//            holder.mOvertimeSurvey1ImageView.setVisibility(View.GONE);
//            holder.mOvertimeSurvey1TextView.setVisibility(View.GONE);
//            switch (otSurvey1Items.get(position)) {
//                case 1:
//                    holder.mOvertimeSurvey1ImageView.setVisibility(View.VISIBLE);
//                    holder.mOvertimeSurvey1TextView.setVisibility(View.GONE);
//                    break;
//                case 2:
//                    holder.mOvertimeSurvey1ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey1TextView.setText("　加班　");
//                    holder.mOvertimeSurvey1TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey1TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 3:
//                    holder.mOvertimeSurvey1ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey1TextView.setText("　私務　");
//                    holder.mOvertimeSurvey1TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey1TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 4:
//                    holder.mOvertimeSurvey1ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey1TextView.setText("加班(代)");
//                    holder.mOvertimeSurvey1TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey1TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 5:
//                    holder.mOvertimeSurvey1ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey1TextView.setText("私務(代)");
//                    holder.mOvertimeSurvey1TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey1TextView.setVisibility(View.VISIBLE);
//                    break;
//                default:
//                    break;
//            }
//
//            //先隱藏 mOvertimeSurvey2ImageView 和 mOvertimeSurvey2TextView
//            holder.mOvertimeSurvey2ImageView.setVisibility(View.GONE);
//            holder.mOvertimeSurvey2TextView.setVisibility(View.GONE);
//            switch (otSurvey2Items.get(position)) {
//                case 1:
//                    holder.mOvertimeSurvey2ImageView.setVisibility(View.VISIBLE);
//                    holder.mOvertimeSurvey2TextView.setVisibility(View.GONE);
//                    break;
//                case 2:
//                    holder.mOvertimeSurvey2ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey2TextView.setText("　加班　");
//                    holder.mOvertimeSurvey2TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey2TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 3:
//                    holder.mOvertimeSurvey2ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey2TextView.setText("　私務　");
//                    holder.mOvertimeSurvey2TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey2TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 4:
//                    holder.mOvertimeSurvey2ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey2TextView.setText("加班(代)");
//                    holder.mOvertimeSurvey2TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey2TextView.setVisibility(View.VISIBLE);
//                    break;
//                case 5:
//                    holder.mOvertimeSurvey2ImageView.setVisibility(View.GONE);
//                    holder.mOvertimeSurvey2TextView.setText("私務(代)");
//                    holder.mOvertimeSurvey2TextView.setTextColor(getResources().getColor(R.color.text_enabled_false_gray));
//                    holder.mOvertimeSurvey2TextView.setVisibility(View.VISIBLE);
//                    break;
//                default:
//                    break;
//            }
//
//            //mPunchclockRecordClickAreaConstraintLayout 被點擊後觸發
//            holder.mPunchclockRecordClickAreaConstraintLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //彈出新增申請單選擇視窗  todo 到時候要連同方法拿掉
////                    showSelectRequisitionAlertDialog();
//
//                    //顯示人員打卡記錄
//                    showPunchclockRecord();
//                }
//            });
//
//
//            //最多超時調查案小圖按鈕有三個
//            holder.mOvertimeSurvey0ImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //先組合好日期後再傳進去超時調查頁面
//                    String dateStr = mCurrentYear + "/" + mCurrentMonth + "/" + dayDateItems.get(position) + "(" + weekDateItems.get(position) + ")";
//
//                    //顯示超時調查
//                    showOvertimeSurvey(attNoItems.get(position), dateStr, 0);
//
//                    //todo 到時候 mWorkdayApplyListDialog.showDialog 這功能 要移到其他地方
////                    mWorkdayApplyListDialog.showDialog(applyStatusItems.get(position));
//                }
//            });
//
//            holder.mOvertimeSurvey1ImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //先組合好日期後再傳進去超時調查頁面
//                    String dateStr = mCurrentYear + "/" + mCurrentMonth + "/" + dayDateItems.get(position) + "(" + weekDateItems.get(position) + ")";
//
//                    //顯示超時調查
//                    showOvertimeSurvey(attNoItems.get(position), dateStr, 1);
//                }
//            });
//
//            holder.mOvertimeSurvey2ImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //先組合好日期後再傳進去超時調查頁面
//                    String dateStr = mCurrentYear + "/" + mCurrentMonth + "/" + dayDateItems.get(position) + "(" + weekDateItems.get(position) + ")";
//
//                    //顯示超時調查
//                    showOvertimeSurvey(attNoItems.get(position), dateStr, 2);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return bikeRentItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //測試新增 點擊 item
            private View mItemView;

            private TextView mStopIdTextView;
            private TextView mNameTextView;
            private TextView mStatusTextView;

            private TextView mCanUseBikeNumberTextView;
            private TextView mCanDropOffBikeNumberTextView;

            private ViewHolder(View itemView) {
                super(itemView);

                //測試新增 點擊 item
                mItemView = itemView;

                mStopIdTextView = itemView.findViewById(R.id.textView_stop_id);
                mNameTextView = itemView.findViewById(R.id.textView_name);
                mStatusTextView = itemView.findViewById(R.id.textView_status);

                mCanUseBikeNumberTextView = itemView.findViewById(R.id.textView_can_use_bike_number);
                mCanDropOffBikeNumberTextView = itemView.findViewById(R.id.textView_can_drop_off_bike_number);
            }
        }
    }

    private String responseDataStr = "[{\"stop_id\":\"0328\",\"name\":\"一江公園\",\"E_name\":\"Yijiang Park\",\"lon\":121.53146362304688,\"lat\":25.053159713745117,\"Address\":\"一江街 / 松江路132巷口(西北側)(鄰近四平陽光商圈)\",\"E_Address\":\"Yijiang St. /  Ln. 132, Songjiang Rd. intersection\",\"city_id\":\"02\",\"area\":\"中山區\",\"E_area\":\"Zhongshan Dist.\",\"SumSpace\":36,\"Vehicles\":9,\"ParkingSpaces\":27,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"2006\",\"name\":\"銀河廣場\",\"E_name\":\"Galaxy Square\",\"lon\":121.2242431640625,\"lat\":24.961715698242188,\"Address\":\"九和一街48號對面銀河廣場人行道\",\"E_Address\":\"No.48, Jiuhe 1st St. (opposite)\",\"city_id\":\"04\",\"area\":\"中壢區\",\"E_area\":\"Zhongli Dist.\",\"SumSpace\":58,\"Vehicles\":23,\"ParkingSpaces\":33,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.773\"},{\"stop_id\":\"3214\",\"name\":\"臺中轉運站\",\"E_name\":\"Taichung Bus Station\",\"lon\":120.68755340576172,\"lat\":24.138870239257812,\"Address\":\"八德街/武德街口(停車場東南側)\",\"E_Address\":\"Bade St & Wude St Intersection(Southeast Side Parking Lot)\",\"city_id\":\"08\",\"area\":\"東區\",\"E_area\":\"East Dist.\",\"SumSpace\":52,\"Vehicles\":26,\"ParkingSpaces\":26,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.903\"},{\"stop_id\":\"0015\",\"name\":\"饒河夜市\",\"E_name\":\"Raohe Night Market\",\"lon\":121.57188415527344,\"lat\":25.049844741821289,\"Address\":\"八德路/松信路(西南側)\",\"E_Address\":\"The S.W. side of St.Wuchang & Road Longjiang.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":60,\"Vehicles\":44,\"ParkingSpaces\":16,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0197\",\"name\":\"饒河夜市(八德路側)\",\"E_name\":\"Raohe Night Market\",\"lon\":121.57188415527344,\"lat\":25.049844741821289,\"Address\":\"八德路/松信路(西南側)\",\"E_Address\":\"The S.W. side of St.Wuchang & Road Longjiang.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":34,\"Vehicles\":14,\"ParkingSpaces\":20,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0231\",\"name\":\"內政部營建署\",\"E_name\":\"Construction & Planning Agency\",\"lon\":121.54502105712891,\"lat\":25.047805786132812,\"Address\":\"八德路二段342號東側人行道(鄰近微風廣場/黑松世界)\",\"E_Address\":\"No.342, Sec. 2, Bade Rd. (east side)\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":40,\"Vehicles\":3,\"ParkingSpaces\":37,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0333\",\"name\":\"復盛公園\",\"E_name\":\"Fusheng Park\",\"lon\":121.56118011474609,\"lat\":25.047428131103516,\"Address\":\"八德路四段106巷6弄2號(東側)(鄰近京華城/中保寶貝城(BabyBoss City)/台北機廠)\",\"E_Address\":\"No.2, Aly. 6, Ln. 106, Sec. 4, Bade Rd.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":28,\"Vehicles\":10,\"ParkingSpaces\":18,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0267\",\"name\":\"八德中坡路口\",\"E_name\":\"Bade & Zhongpo Intersection\",\"lon\":121.58019256591797,\"lat\":25.050619125366211,\"Address\":\"八德路四段869號前方人行道(鄰近饒河街夜市)\",\"E_Address\":\"No.869, Sec. 4, Bade Rd.\",\"city_id\":\"02\",\"area\":\"南港區\",\"E_area\":\"Nangang Dist.\",\"SumSpace\":38,\"Vehicles\":34,\"ParkingSpaces\":4,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0033\",\"name\":\"中崙高中\",\"E_name\":\"Zhonglun High School\",\"lon\":121.56086730957031,\"lat\":25.04878044128418,\"Address\":\"八德路四段91巷(中崙高中)旁(鄰近京華城/中保寶貝城(BabyBoss City))\",\"E_Address\":\"The side of Ln. 91, Sec. 4, Bade Rd. ( beside Zhong-Lun High School)\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":46,\"Vehicles\":25,\"ParkingSpaces\":21,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"3041\",\"name\":\"力行國小\",\"E_name\":\"Li Sing Elementary School\",\"lon\":120.69393157958984,\"lat\":24.151573181152344,\"Address\":\"力行路/進化路口\",\"E_Address\":\"Lixing Rd & Jinhua Rd Intersection\",\"city_id\":\"08\",\"area\":\"東區\",\"E_area\":\"East Dist.\",\"SumSpace\":50,\"Vehicles\":6,\"ParkingSpaces\":44,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.903\"}]";

}
