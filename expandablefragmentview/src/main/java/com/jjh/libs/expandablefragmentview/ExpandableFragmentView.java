package com.jjh.libs.expandablefragmentview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjh.libs.expandablefragmentview.utils.AnimationUtil;

/**
 * Created by jjh860627 on 2017. 6. 14..
 */

public class ExpandableFragmentView extends LinearLayout implements View.OnClickListener{
    private float animDurationWeight = 1.0f;

    private RelativeLayout rlBtnContainer;
    private ImageView ivBtnArrow;
    private TextView tvBtnText;
    private FrameLayout flFragmentContainer;
    private Fragment fragment;

    private boolean collapseSiblingsWhenExpand;

    private boolean isOnToggle = false;

    public ExpandableFragmentView(Context context) {
        super(context);
        initView();
    }

    public ExpandableFragmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public ExpandableFragmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_expandable_layout, this, false);
        addView(v);
        rlBtnContainer = (RelativeLayout) findViewById(R.id.rlBtnContainer);
        ivBtnArrow = (ImageView) findViewById(R.id.ivBtnArrow);
        tvBtnText = (TextView) findViewById(R.id.tvBtnText);
        flFragmentContainer = (FrameLayout) findViewById(R.id.flFragmentContainer);

        rlBtnContainer.setOnClickListener(this);
        flFragmentContainer.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(final View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(isOnToggle || v.getVisibility() == View.GONE) return;
                //Expand 상태에서 자식 뷰의 크기가 변경 될때 같이 변경 될 수 있도록 함.
                v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

                final int targetHeight = v.getMeasuredHeight();

                if(v.getMeasuredHeight() != v.getHeight()) {
                   v.post(new Runnable() {
                        @Override
                        public void run() {
                            v.getLayoutParams().height = targetHeight;
                            v.requestLayout();
                        }
                    });
                }
            }
        });
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableFragmentView);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableFragmentView, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {

        animDurationWeight = typedArray.getFloat(R.styleable.ExpandableFragmentView_animDurationWeight, 1.0f);

        int bgColor = typedArray.getColor(R.styleable.ExpandableFragmentView_bgColor, Color.WHITE);
        rlBtnContainer.setBackgroundColor(bgColor);

        int arrowResId = typedArray.getResourceId(R.styleable.ExpandableFragmentView_arrowImage, -1);
        if(arrowResId > 0) {
            ivBtnArrow.setImageResource(arrowResId);
        }

        int textColor = typedArray.getColor(R.styleable.ExpandableFragmentView_textColor, Color.BLACK);
        tvBtnText.setTextColor(textColor);

        String textStr = typedArray.getString(R.styleable.ExpandableFragmentView_text);
        tvBtnText.setText(textStr);

        boolean isExpanded = typedArray.getBoolean(R.styleable.ExpandableFragmentView_isExpanded, false);
        flFragmentContainer.setVisibility(isExpanded? View.VISIBLE : View.GONE);

        collapseSiblingsWhenExpand = typedArray.getBoolean(R.styleable.ExpandableFragmentView_collapseSiblingsWhenExpand, false);

        String fragmentName = typedArray.getString(R.styleable.ExpandableFragmentView_fragmentName);

        try {
            flFragmentContainer.setId(View.generateViewId());
            fragment = (Fragment)Class.forName(fragmentName).newInstance();
            if(fragment != null) {
                FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(flFragmentContainer.getId(), fragment);
                ft.commit();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        typedArray.recycle();
    }

    @Override
    public void onClick(View v) {
        if(v == rlBtnContainer){
            toggle();
        }
    }

    public void setText(String text){
        if(text != null) {
            tvBtnText.setText(text);
        }
    }

    public void setText(int textResId){
        tvBtnText.setText(getContext().getString(textResId));
    }

    public void setArrowImage(Drawable drawable){
        ivBtnArrow.setBackground(drawable);
    }

    public void setArrowImage(int drawableResId){
        ivBtnArrow.setBackgroundResource(drawableResId);
    }

    public void setBackgroundColor(int color){
        rlBtnContainer.setBackgroundColor(color);
    }

    public void toggle(){
        if(isExpanded()) {
            collapse();
        }else{
            expand();
        }
    }

    public void expand(){
        isOnToggle = true;
        AnimationUtil.expandLayoutWithRotateView(flFragmentContainer, ivBtnArrow, animationListener, animDurationWeight);
        if(collapseSiblingsWhenExpand) {
            ViewGroup parentView = (ViewGroup) this.getParent();
            for (int i = 0; i < parentView.getChildCount(); i++) {
                View childView = parentView.getChildAt(i);
                if (childView instanceof ExpandableFragmentView && childView != this && ((ExpandableFragmentView) childView).isExpanded()) {
                    ((ExpandableFragmentView) childView).collapse();
                }
            }
        }
    }

    public void collapse(){
        isOnToggle = true;
        AnimationUtil.collapseLayoutWithRotateView(flFragmentContainer, ivBtnArrow, animationListener, animDurationWeight);
    }

    public boolean isExpanded(){
        return flFragmentContainer.getVisibility() == View.VISIBLE;
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            isOnToggle = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };
}
