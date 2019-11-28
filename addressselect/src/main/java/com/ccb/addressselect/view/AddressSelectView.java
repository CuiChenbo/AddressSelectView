package com.ccb.addressselect.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccb.addressselect.R;
import com.ccb.addressselect.bean.AddressBean;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddressSelectView extends RelativeLayout {

    private LinearLayout tab;
    private RecyclerView rv;
    private Context mC;
    private AddressBean addressBean;//数据源
    private List<AddressBean.AddressItemBean> addressDatas; //当前正在选择的数据
    private List<AddressBean.AddressItemBean> alreadySelectDatas; //已经选择的数据

    public AddressSelectView(Context context) {
        super(context);
        init(context);
    }

    public AddressSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private View rootView;

    private void init(Context context) {
        mC = context;
        rootView = View.inflate(context, R.layout.activity_address_select, this);
        tab = rootView.findViewById(R.id.tab);
        rv = rootView.findViewById(R.id.rv);
        initList();
        initData();
        initView();
    }

    private String clickText; //回退时点击的文字

    private void initList() {
        rootView.findViewById(R.id.tvQ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectConfirmListEner != null) onSelectConfirmListEner.onConfirm(new Gson().toJson(alreadySelectDatas));
            }
        });
        setOnTabClickListEner(new OnTabClickListEner() {
            @Override
            public void onTabClick(View view, int position) {
                if (alreadySelectDatas == null || position == tab.getChildCount() - 1) return;
                if (alreadySelectDatas.size() > position)
                    clickText = alreadySelectDatas.get(position).getName();
                if (position == 0) {
                    //如果点击第一个，就重新开始选择
                    if (alreadySelectDatas != null) alreadySelectDatas.clear();
                    tab.removeAllViews();
                    addRadioButton("选择省");
                    if (addressBean != null) {
                        addressAdapter.setDatas(addressBean.getProvince());
                    }
                } else {
                    //如果点击的不是第一个，则刷新数据
                    List<AddressBean.AddressItemBean> alreadySelectDatas2 = new ArrayList<>();
                    if (alreadySelectDatas.size() >= position) {
                        for (int i = 0; i < position; i++) {
                            //只保留点击的这个之前的数据；
                            alreadySelectDatas2.add(alreadySelectDatas.get(i));
                        }
                    }
                    alreadySelectDatas.clear();
                    alreadySelectDatas.addAll(alreadySelectDatas2);
                    //使用新的数据重新规划数据源和UI
                    tab.removeAllViews();
                    for (int j = 0; j < alreadySelectDatas.size(); j++) {
                        addRadioButton(alreadySelectDatas.get(j).getName());
                    }
                    selectPostion(alreadySelectDatas.get(alreadySelectDatas.size() - 1), false);
                }
            }
        });
    }


    private AddressAdapter addressAdapter;

    private void initView() {
        addRadioButton("选择省");
        rv.setLayoutManager(new LinearLayoutManager(mC));
        addressAdapter = new AddressAdapter(mC, null);
        rv.setAdapter(addressAdapter);
        if (addressBean != null) {
            addressAdapter.setDatas(addressBean.getProvince());
        }
    }

    /**
     * 初始化数据
     * 拿assets下的json文件
     */
    private void initData() {
        StringBuilder jsonSB = new StringBuilder();
        try {
            BufferedReader addressJsonStream = new BufferedReader(new InputStreamReader(mC.getAssets().open("address.json")));
            String line;
            while ((line = addressJsonStream.readLine()) != null) {
                jsonSB.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将数据转换为对象
        addressBean = new Gson().fromJson(jsonSB.toString(), AddressBean.class);
    }

    /**
     * 添加一个Tab
     *
     * @param text
     * @return
     */
    private void addRadioButton(String text) {
        for (int i = 0; i < tab.getChildCount(); i++) {
            ((TextView) tab.getChildAt(i)).setTextColor(getResources().getColor(R.color.textmain));
        }
        final TextView textView = new TextView(mC);
        textView.setTag(tab.getChildCount());
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(20, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.textselect));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTabClickListEner != null)
                    onTabClickListEner.onTabClick(textView, (int) textView.getTag());
            }
        });
        tab.addView(textView);
    }

    /**
     * 选择一个位置后
     *
     * @param data
     */
    private void selectPostion(AddressBean.AddressItemBean data, boolean isAddData) {
        rv.scrollToPosition(0);
        if (alreadySelectDatas == null) {
            alreadySelectDatas = new ArrayList<>();
        }
        if (isAddData) alreadySelectDatas.add(data);

        if (addressDatas == null) {
            addressDatas = new ArrayList<>();
        } else {
            addressDatas.clear();
        }
        //修改当前选择的名称
        ((TextView) tab.getChildAt(tab.getChildCount() - 1)).setText(data.getName());

        if (tab.getChildCount() == 1) {
            addRadioButton("选择市");
            for (int i = 0; i < addressBean.getCity().size(); i++) {
                if (TextUtils.equals(data.getId(), addressBean.getCity().get(i).getAboveId())) {
                    addressDatas.add(addressBean.getCity().get(i));
                }
            }
        } else if (tab.getChildCount() == 2) {
            addRadioButton("选择区");
            for (int i = 0; i < addressBean.getDistrict().size(); i++) {
                if (TextUtils.equals(data.getId(), addressBean.getDistrict().get(i).getAboveId())) {
                    addressDatas.add(addressBean.getDistrict().get(i));
                }
            }
        }else {
            if (onSelectConfirmListEner != null) onSelectConfirmListEner.onConfirm(new Gson().toJson(alreadySelectDatas));
        }
        addressAdapter.setDatas(addressDatas);
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Vh> {

        private Context context;
        private List<AddressBean.AddressItemBean> datas;

        public AddressAdapter(Context context, List<AddressBean.AddressItemBean> datas) {
            this.context = context;
            if (datas == null) {
                this.datas = new ArrayList<>();
            } else {
                this.datas = datas;
            }
        }

        public void setDatas(List<AddressBean.AddressItemBean> datas) {
            if (datas == null) {
                this.datas = new ArrayList<>();
            } else {
                this.datas = datas;
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Vh(LayoutInflater.from(context).inflate(R.layout.item_address, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Vh holder, final int position) {
            holder.tv.setText(datas.get(position).getName());
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPostion(datas.get(position), true);
                }
            });
            //如果用户点击导航条重新选择的话，就标记当前选择过的颜色加深
            if (TextUtils.equals(datas.get(position).getName(), clickText)) {
                holder.tv.setTextColor(context.getResources().getColor(R.color.textselect));
            } else {
                holder.tv.setTextColor(context.getResources().getColor(R.color.textmain));
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class Vh extends RecyclerView.ViewHolder {

            public TextView tv;

            public Vh(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }


    interface OnTabClickListEner {
        void onTabClick(View view, int position);
    }

    private OnTabClickListEner onTabClickListEner;

    private void setOnTabClickListEner(OnTabClickListEner onTabClickListEner) {
        this.onTabClickListEner = onTabClickListEner;
    }

    public interface OnSelectConfirmListEner {
        void onConfirm(String s);
    }

    public OnSelectConfirmListEner onSelectConfirmListEner;

    public void setOnSelectConfirmListEner(OnSelectConfirmListEner onSelectConfirmListEner) {
        this.onSelectConfirmListEner = onSelectConfirmListEner;
    }
}
