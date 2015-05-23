package cn.artobj.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by rsmac on 15/1/29.
 */
public class ArtButtonImage extends LinearLayout  {
    private LinearLayout rootLayout;
    private ImageView icoImage;
    private TextView titleText;
    private Context context;
    public ArtButtonImage(Context context, AttributeSet attrs) {
        super(context, attrs);
//        this.context=context;
//        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.art_button_image, this);
//        rootLayout= (LinearLayout) findViewById(R.id.rootLayout);
//        icoImage= (ImageView) findViewById(R.id.icoImage);
//        titleText= (TextView) findViewById(R.id.titleText);

//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArtButtonImage);
//        if(LinearLayout.VERTICAL==array.getInt(R.attr.orientation, LinearLayout.HORIZONTAL)){
//            rootLayout.setOrientation(LinearLayout.VERTICAL);
//        }




    }

    public void setOnClickLinstener(OnClickListener listener){
        rootLayout.setOnClickListener(listener);
    }
}
