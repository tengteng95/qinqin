package com.bmob.im.demo.ui.fragment;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.FragmentBase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegisterByPhoneFragment extends FragmentBase{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_register_phone, container, false);
	}
}
