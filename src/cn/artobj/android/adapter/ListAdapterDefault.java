package cn.artobj.android.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import cn.artobj.R;
import cn.artobj.object.AOMap;
import cn.artobj.json.JSONObject;

import java.util.HashMap;

/**
 * Created by rsmac on 15/1/30.
 */
public class ListAdapterDefault<ViewHolderDefault> extends ListAdapter {

    public ListAdapterDefault(Activity activity) {
        super(activity);
    }

    public ListAdapterDefault(AOMap para, Activity activity) {
        super(para, activity);
    }

    @Override
    public int initLayoutID() {
        return R.layout.binlistitem;
    }

    @Override
    public ViewHolder initViewHolder() {
        return null;
    }


    @Override
    public void updateUI(View convertView, ViewHolder obj) {
        TextView firstView=(TextView)convertView.findViewById(R.id.first);
        firstView.setMaxLines(2);
        firstView.setText(obj.getString("text"));
    }

    public class ViewHolderDefault extends ViewHolder{

    }
}


