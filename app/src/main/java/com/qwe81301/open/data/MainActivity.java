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
import com.qwe81301.open.data.interfaceutil.OnSignDialog3ResultListener;
import com.qwe81301.open.data.util.RecyclerViewNoBugLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private OkHttp mOkHttp;
    private String responseStr = "";

    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    private FeedbackDialog mFeedbackDialog;

    private TextView mFeedbackTextView;

    private int mTotalShopCount;// 計算總站數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOkHttp = new OkHttp(getApplicationContext(), this);
        mFeedbackDialog = new FeedbackDialog(MainActivity.this, MainActivity.this);

        mFeedbackTextView = findViewById(R.id.textView_feed_back);

        //RecyclerView的初始化
        mRecyclerView = findViewById(R.id.recyclerView_bike_record);

        setRecyclerView();

        //請求 Open Data
        requestOpenData();
    }

    private void setRecyclerView() {
        //設置LayoutManager
        mRecyclerView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getApplicationContext()));

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
    }

    /**
     * 請求 Open Data
     */
    private void requestOpenData() {
        service.submit(new Runnable() {//todo(提醒)  新增一個 執行緒跑有關網路的連線 OkHttp
            @Override
            public void run() {

                //todo 使用網址 https://www.databar.com.tw/ListApi/ApiLink#%E8%87%AA%E8%A1%8C%E8%BB%8A%E7%A7%9F%E5%80%9F%E8%B3%87%E6%96%99
                responseStr = mOkHttp.get("https://www.easytraffic.com.tw/OpenService/Bike/BikeRentData?$top=20");

                try {
                    Log.v("TEST", "responseStr:" + responseStr);

                    //todo 如果資料不是每次都能順利拿到 到時候用放在 最下面 用固定的資料
                    Gson gson = new Gson();

                    //字串轉成list
                    List<BikeRentDataBean> bikeRentDataBeanForList = gson.fromJson(responseStr, new TypeToken<List<BikeRentDataBean>>() {
                    }.getType());

                    mTotalShopCount = 0;

                    for (int i = 0, q = bikeRentDataBeanForList.size(); i < q; i++) {
                        mTotalShopCount++;
                    }

                    mAdapter = new MyAdapter(
                            bikeRentDataBeanForList
                    );

                    //todo 修改畫面的動作 要再 UiThread 中執行
                    //如果UI執行緒blocked的時間太長(大約超過5秒),使用者就會看到ANR(application not responding)的對話方塊。
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFeedbackTextView.setText("總站數：" + mTotalShopCount);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
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

    //todo 使用RecyclerView(取代list View) 最主要放資料進列表的的部分
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<BikeRentDataBean> bikeRentItems;

        private MyAdapter(List<BikeRentDataBean> bikeRentItems) {
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

            holder.mStopIdTextView.setText(bikeRentItems.get(position).getStop_id());
            holder.mNameTextView.setText(bikeRentItems.get(position).getName());

            holder.mCanUseBikeNumberTextView.setText("可借：" + bikeRentItems.get(position).getVehicles());
            holder.mCanDropOffBikeNumberTextView.setText("可還：" + bikeRentItems.get(position).getParkingSpaces());

            //todo(提示用) java  int 強制轉型 成 double
            double sumSpace = (double) bikeRentItems.get(position).getSumSpace();
            double vehicles = (double) bikeRentItems.get(position).getVehicles();

            //計算 比例
            double persentCalcurlate = (vehicles / sumSpace);

            //todo(提示用)  這邊我自訂一下 腳踏車站的車況稍微分成三類   (1 ~ 0.4)  (0.4 ~ 0)  (0)
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

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo(提醒) 用 callback的寫法 回傳data
                    mFeedbackDialog.showDialog(new OnSignDialog3ResultListener() {
                        @Override
                        public void dialogPositiveResult(String note) {
                            mFeedbackTextView.setText(note);
                        }

                        @Override
                        public void dialogNegativeResult(String note) {
                            mFeedbackTextView.setText(note);
                        }

                        @Override
                        public void dialogNeutralResult() {
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return bikeRentItems.size();
        }

        //todo(提示用) RecyclerView 元件宣告
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

    //講解時如果 api 不穩的話 換成這固定資料使用
    private String responseDataStr = "[{\"stop_id\":\"0328\",\"name\":\"一江公園\",\"E_name\":\"Yijiang Park\",\"lon\":121.53146362304688,\"lat\":25.053159713745117,\"Address\":\"一江街 / 松江路132巷口(西北側)(鄰近四平陽光商圈)\",\"E_Address\":\"Yijiang St. /  Ln. 132, Songjiang Rd. intersection\",\"city_id\":\"02\",\"area\":\"中山區\",\"E_area\":\"Zhongshan Dist.\",\"SumSpace\":36,\"Vehicles\":9,\"ParkingSpaces\":27,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"2006\",\"name\":\"銀河廣場\",\"E_name\":\"Galaxy Square\",\"lon\":121.2242431640625,\"lat\":24.961715698242188,\"Address\":\"九和一街48號對面銀河廣場人行道\",\"E_Address\":\"No.48, Jiuhe 1st St. (opposite)\",\"city_id\":\"04\",\"area\":\"中壢區\",\"E_area\":\"Zhongli Dist.\",\"SumSpace\":58,\"Vehicles\":23,\"ParkingSpaces\":33,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.773\"},{\"stop_id\":\"3214\",\"name\":\"臺中轉運站\",\"E_name\":\"Taichung Bus Station\",\"lon\":120.68755340576172,\"lat\":24.138870239257812,\"Address\":\"八德街/武德街口(停車場東南側)\",\"E_Address\":\"Bade St & Wude St Intersection(Southeast Side Parking Lot)\",\"city_id\":\"08\",\"area\":\"東區\",\"E_area\":\"East Dist.\",\"SumSpace\":52,\"Vehicles\":26,\"ParkingSpaces\":26,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.903\"},{\"stop_id\":\"0015\",\"name\":\"饒河夜市\",\"E_name\":\"Raohe Night Market\",\"lon\":121.57188415527344,\"lat\":25.049844741821289,\"Address\":\"八德路/松信路(西南側)\",\"E_Address\":\"The S.W. side of St.Wuchang & Road Longjiang.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":60,\"Vehicles\":44,\"ParkingSpaces\":16,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0197\",\"name\":\"饒河夜市(八德路側)\",\"E_name\":\"Raohe Night Market\",\"lon\":121.57188415527344,\"lat\":25.049844741821289,\"Address\":\"八德路/松信路(西南側)\",\"E_Address\":\"The S.W. side of St.Wuchang & Road Longjiang.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":34,\"Vehicles\":14,\"ParkingSpaces\":20,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0231\",\"name\":\"內政部營建署\",\"E_name\":\"Construction & Planning Agency\",\"lon\":121.54502105712891,\"lat\":25.047805786132812,\"Address\":\"八德路二段342號東側人行道(鄰近微風廣場/黑松世界)\",\"E_Address\":\"No.342, Sec. 2, Bade Rd. (east side)\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":40,\"Vehicles\":3,\"ParkingSpaces\":37,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0333\",\"name\":\"復盛公園\",\"E_name\":\"Fusheng Park\",\"lon\":121.56118011474609,\"lat\":25.047428131103516,\"Address\":\"八德路四段106巷6弄2號(東側)(鄰近京華城/中保寶貝城(BabyBoss City)/台北機廠)\",\"E_Address\":\"No.2, Aly. 6, Ln. 106, Sec. 4, Bade Rd.\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":28,\"Vehicles\":10,\"ParkingSpaces\":18,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0267\",\"name\":\"八德中坡路口\",\"E_name\":\"Bade & Zhongpo Intersection\",\"lon\":121.58019256591797,\"lat\":25.050619125366211,\"Address\":\"八德路四段869號前方人行道(鄰近饒河街夜市)\",\"E_Address\":\"No.869, Sec. 4, Bade Rd.\",\"city_id\":\"02\",\"area\":\"南港區\",\"E_area\":\"Nangang Dist.\",\"SumSpace\":38,\"Vehicles\":34,\"ParkingSpaces\":4,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"0033\",\"name\":\"中崙高中\",\"E_name\":\"Zhonglun High School\",\"lon\":121.56086730957031,\"lat\":25.04878044128418,\"Address\":\"八德路四段91巷(中崙高中)旁(鄰近京華城/中保寶貝城(BabyBoss City))\",\"E_Address\":\"The side of Ln. 91, Sec. 4, Bade Rd. ( beside Zhong-Lun High School)\",\"city_id\":\"02\",\"area\":\"松山區\",\"E_area\":\"Songshan Dist.\",\"SumSpace\":46,\"Vehicles\":25,\"ParkingSpaces\":21,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.143\"},{\"stop_id\":\"3041\",\"name\":\"力行國小\",\"E_name\":\"Li Sing Elementary School\",\"lon\":120.69393157958984,\"lat\":24.151573181152344,\"Address\":\"力行路/進化路口\",\"E_Address\":\"Lixing Rd & Jinhua Rd Intersection\",\"city_id\":\"08\",\"area\":\"東區\",\"E_area\":\"East Dist.\",\"SumSpace\":50,\"Vehicles\":6,\"ParkingSpaces\":44,\"IsService\":1,\"et_update\":\"2019-09-20T18:02:03.903\"}]";

}
