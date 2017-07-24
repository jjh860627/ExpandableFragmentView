package com.jjh.libs.exapandablefragmentviewdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjh.libs.exapandablefragmentviewdemo.R;

/**
 * Created by jjh860627 on 2017. 7. 24..
 */

public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, null);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
