package cn.artobj.android.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.android.app.AppDefault;
import cn.softwaredata.rsaggie.R;

public class ArtListControlViewPage extends ArtListControlPage {
	private final String tag="ArtListControlViewPage";
	private final RotateAnimation animation;
	private final ImageView arrowImageView;
	private View headerView;
	private View footerView;
	private LayoutInflater inflater;
	private ListView listView;


	public ArtListControlViewPage(Activity window,ListAdapter adapter,ListView listView){
		super(window,adapter);
		this.listView=listView;
		inflater=LayoutInflater.from(AppDefault.getAppContext());
		footerView = (View) inflater.inflate(R.layout.artlistview_footer,null);
		headerView = (View) inflater.inflate(R.layout.artlistview_head,null);
//		listView.addHeaderView(headerView);
		this.listView.addFooterView(footerView);
		this.listView.setAdapter(this.adapter);
		this.listView.setOnScrollListener(this);

		arrowImageView = (ImageView) headerView
				.findViewById(R.id.head_arrowImageView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

//		arrowImageView.setAnimation(animation);
	}



	@Override
	protected void loadData() {
		if(this.listView.getFooterViewsCount()==0)
			this.listView.addFooterView(footerView);
		super.loadData();
	}

	protected void finishLoadData(){
		this.listView.removeFooterView(footerView);
	}
}
