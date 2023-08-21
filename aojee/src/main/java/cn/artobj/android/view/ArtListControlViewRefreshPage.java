package cn.artobj.android.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.animation.RotateAnimation;
import android.widget.*;
import cn.artobj.R;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.android.app.AOLog;
import cn.artobj.android.app.AppDefault;
import cn.artobj.object.AOList;

/**
 * Created by rsmac on 15/9/18.
 */
public class ArtListControlViewRefreshPage extends ArtListControlViewPage implements View.OnTouchListener,ArtListControlPage.OnControlPageListener {
    public static final int STATUS_PULL_TO_REFRESH = 0; //下拉状态
    public static final int STATUS_RELEASE_TO_REFRESH = 1;//释放立即刷新状态
    public static final int STATUS_REFRESHING = 2;//正在刷新状态
    public static final int STATUS_REFRESH_FINISHED = 3;//刷新完成或未刷新状态
    public static final int SCROLL_SPEED = -20;//下拉头部回滚的速度
    public static final long ONE_MINUTE = 60 * 1000;//一分钟的毫秒值，用于判断上次的更新时间
    public static final long ONE_HOUR = 60 * ONE_MINUTE;//一小时的毫秒值，用于判断上次的更新时间
    public static final long ONE_DAY = 24 * ONE_HOUR;//一天的毫秒值，用于判断上次的更新时间
    public static final long ONE_MONTH = 30 * ONE_DAY;//一月的毫秒值，用于判断上次的更新时间
    public static final long ONE_YEAR = 12 * ONE_MONTH;//一年的毫秒值，用于判断上次的更新时间
    private static final String UPDATED_AT = "updated_at"; //上次更新时间的字符串常量，用于作为SharedPreferences的键值
    private PullToRefreshListener mListener; //下拉刷新的回调接口
    private SharedPreferences preferences;//用于存储上次更新时间
    private ProgressBar progressBar; //刷新时显示的进度条
    private ImageView arrow; //指示下拉和释放的箭头
    private TextView description;//指示下拉和释放的文字描述
    private TextView updateAt;//上次更新时间的文字描述
    private RelativeLayout.MarginLayoutParams headerLayoutParams;//下拉头的布局参数
    private long lastUpdateTime;//上次更新时间的毫秒值
    private int mId = -1;//为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
    private int hideHeaderHeight;//下拉头的高度
    private int currentStatus = STATUS_REFRESH_FINISHED;//当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
    private int lastStatus = currentStatus;//记录上一次的状态是什么，避免进行重复操作
    private float yDown;//手指按下时的屏幕纵坐标
    private int touchSlop;//在被判定为滚动之前用户手指可以移动的最大值。
    private boolean loadOnce;//是否已加载过一次layout，这里onLayout中的初始化只需加载一次
    private boolean ableToPull;//当前是否可以下拉，只有ListView滚动到头的时候才允许下拉
    private RelativeLayout refreshView;

    private boolean isloaded=false;

    public ArtListControlViewRefreshPage(Activity window, ListAdapter adapter, ListView listView) {
        super(window, adapter, listView);
        preferences = PreferenceManager.getDefaultSharedPreferences(window);
        LinearLayout header=new LinearLayout(window);
        header.setGravity(Gravity.CENTER);
        listView.addHeaderView(header);

        refreshView= (RelativeLayout) LayoutInflater.from(AppDefault.getAppContext()).inflate(R.layout.pull_to_refresh, null);
        header.addView(refreshView);
        progressBar = (ProgressBar) refreshView.findViewById(R.id.progress_bar);
        arrow = (ImageView) refreshView.findViewById(R.id.arrow);
        description = (TextView) refreshView.findViewById(R.id.description);
        updateAt = (TextView) refreshView.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(window).getScaledTouchSlop();
        refreshUpdatedAtValue();
        if (!loadOnce) {
            hideHeaderHeight = -120;
            headerLayoutParams = (RelativeLayout.MarginLayoutParams) refreshView.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            refreshView.setLayoutParams(headerLayoutParams);
            loadOnce = true;
        }
        this.setListener(this);

    }

    public void onRefresh() {
        page=1;
        if(mListener!=null){
            mListener.loadMoreData(true,page,pageSize);
        }
    }

    @Override
    public void load() {
        isloaded=false;
        listView.setOnTouchListener(null);
        super.load();
    }

    public synchronized void notifyDataChanged(final AOList tmpList){
        super.notifyDataChanged(tmpList);
        finishRefreshing();
        if(page>1){
            if(!isloaded){
                listView.setOnTouchListener(this);
                isloaded=true;
            }
        }else {
            listView.setOnTouchListener(null);
        }
    }


    /**
     * 当ListView被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    // 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if (distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                        return false;
                    }
                    if (distance < touchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        // 通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                        refreshView.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        runHideHeaderTask();
                    }
                    break;
            }
            // 时刻记得更新下拉头中的信息
            if (currentStatus == STATUS_PULL_TO_REFRESH
                    || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
                return true;
            }
        }
        return false;
    }

    /**
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener
     *            监听器的实现。
     * @param id
     *            为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
        runHideHeaderTask();
    }

    public void runHideHeaderTask(){
        if(headerLayoutParams!=null){
            new HideHeaderTask().execute();
        }
    }

    /**
     * 根据当前ListView的滚动状态来设定 {@link #ableToPull}
     * 的值，每次都需要在onTouch中第一个执行，这样可以判断出当前应该是滚动ListView，还是应该进行下拉。
     *
     * @param event
     */
    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = listView.getFirstVisiblePosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    refreshView.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新
            ableToPull = true;
        }
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                description.setText(window.getResources().getString(R.string.pull_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                description.setText(window.getResources().getString(R.string.release_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                description.setText(window.getResources().getString(R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
            }
            refreshUpdatedAtValue();
        }
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    /**
     * 刷新下拉头中上次更新时间的文字描述。
     */
    private void refreshUpdatedAtValue() {
        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = window.getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = window.getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = window.getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(window.getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(window.getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(window.getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(window.getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(window.getResources().getString(R.string.updated_at), value);
        }
        updateAt.setText(updateAtValue);
    }

    @Override
    public void loadMoreData(int page, int pageSize) {
        if(mListener!=null){
            mListener.loadMoreData(false,page,pageSize);
        }

    }

    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     *
     * @author guolin
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            onRefresh();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            refreshView.setLayoutParams(headerLayoutParams);
        }

    }

    /**
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     *
     * @author guolin
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            refreshView.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            refreshView.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param time
     *            指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     *
     * @author guolin
     */
    public interface PullToRefreshListener {
        void loadMoreData(boolean isRefresh,int page,int pageSize);

    }


}

