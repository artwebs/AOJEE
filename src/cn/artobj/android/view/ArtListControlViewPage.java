package cn.artobj.android.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.artobj.R;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.android.app.AppDefault;
import cn.artobj.object.AOList;

public class ArtListControlViewPage extends ArtListControlPage {
	private final String tag="ArtListControlViewPage";
	private final RotateAnimation animation;
	protected View headerView;
	private View footerView;
	private LayoutInflater inflater;
	protected ListView listView;


	public ArtListControlViewPage(Activity window,ListAdapter adapter,ListView listView){
		super(window,adapter);
		this.listView=listView;
		inflater=LayoutInflater.from(AppDefault.getAppContext());
		this.listView.setAdapter(this.adapter);
		this.listView.setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

//		arrowImageView.setAnimation(animation);
	}

	private void initFootView(){
		if(footerView!=null){
			this.listView.removeFooterView(footerView);
		}
		this.listView.addFooterView(LayoutInflater.from(AppDefault.getAppContext()).inflate(R.layout.artlistview_footer,null));
	}

	private void errorFootView(){
		if(footerView!=null){
			this.listView.removeFooterView(footerView);
		}
		this.listView.addFooterView(LayoutInflater.from(AppDefault.getAppContext()).inflate(R.layout.artlistview_footer,null));
	}


	@Override
	public void setItemToClass(Class itemToClass) {
		super.setItemToClass(itemToClass);
		this.listView.setOnItemClickListener(this);
	}

	@Override
	protected void loadData() {
		if(this.listView.getFooterViewsCount()==0)
			this.listView.addFooterView(footerView);
		super.loadData();
	}

	@Override
	public synchronized void notifyDataChanged(AOList tmpList) {
		super.notifyDataChanged(tmpList);
		if(page==1){
			footerView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
		}
	}

	protected void finishLoadData(){
		this.listView.removeFooterView(footerView);
	}
}
