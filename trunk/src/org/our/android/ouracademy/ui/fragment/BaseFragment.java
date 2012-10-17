package org.our.android.ouracademy.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
*
* @author JiHoon, Moon on NTS
*
*/
public class BaseFragment extends Fragment {
	//Fragment가 Activity에 포함되는 순간. 호출
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	//Fragment가 생성될 때 호출
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	//Fragment의 UI를 구성하는 뷰를 반환
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	//Fragment가 화면에 표시될때 호출.
	@Override
	public void onStart() {
		super.onStart();
	}
	//Fragment가 사용자와 상호작용 할수 있게 되었을 때 호출(즉 Fragment가 완전히 화면에 표시 되었을 때)
	@Override
	public void onResume() {
		super.onResume();
	}
	//Fragment가 사용자와 상호작용을 할수 없게 되었을 때 호출(화면에는 표시되고 있으나 다른 요소에 의하 상호작용을 못할 때)
	@Override
	public void onPause() {
		super.onPause();
	}
	//Fragment가 화면에 표시되지 않을때 호출
	@Override
	public void onStop() {
		super.onStop();
	}
	//Fragment가 화면에 사라진후, 뷰의 현재 상태가 저장된 후 호출(Fragment와 관련된 뷰를 해제한다.)
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	//Fragment가 더 이상 사용되지 않을 때 호출
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	//Fragment가 Activity에서 제거 될때 호출.
	@Override
	public void onDetach(){
		super.onDetach();
	}
}
