package com.example.commoditymanagerment.Fragment.GoodsCategory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.commoditymanagerment.R;

public class ActivityFragment extends Fragment {

    private View mView ;
    private Context mContext ;
    private Activity mActivity ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fg_category_activity ,container ,false) ;
        mContext = getContext() ;
        mActivity = getActivity() ;
        return mView;
    }

}
