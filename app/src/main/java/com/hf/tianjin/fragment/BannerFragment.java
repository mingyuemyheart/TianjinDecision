package com.hf.tianjin.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hf.tianjin.R;

import net.tsz.afinal.FinalBitmap;


/**
 * 广告位
 */

public class BannerFragment extends Fragment {

    private ImageView imageView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banner, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        imageView = (ImageView) view.findViewById(R.id.imageView);

        String imgUrl = getArguments().getString("imgUrl");
        if (!TextUtils.isEmpty(imgUrl)) {
            FinalBitmap finalBitmap = FinalBitmap.create(getActivity());
            finalBitmap.display(imageView, imgUrl, null, 0);
        }
    }

}
