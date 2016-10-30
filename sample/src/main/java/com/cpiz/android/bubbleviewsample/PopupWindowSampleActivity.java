package com.cpiz.android.bubbleviewsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.cpiz.android.bubbleview.RelativePos;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cpiz.android.bubbleview.Utils.dp2px;

public class PopupWindowSampleActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "PopupWindowSample";

    @BindView(R.id.activity_popup_window_sample)
    ViewGroup mActivityPopupWindowSample;
    @BindView(R.id.layout_anchor)
    ViewGroup mLayoutAnchor;
    @BindView(R.id.edit_sample_text)
    EditText mEditText;
    @BindView(R.id.seekbar_anchor_side_length)
    SeekBar mSeekbarAnchorSideLength;
    @BindView(R.id.view_anchor)
    TextView mAnchorView;
    @BindView(R.id.checkbox_cancel_on_touch)
    CheckBox mCheckboxCancelOnTouch;
    @BindView(R.id.checkbox_cancel_on_touch_outside)
    CheckBox mCheckboxCancelOnTouchOutside;
    @BindView(R.id.seekbar_auto_cancel_time)
    SeekBar mSeekbarAutoCancelTime;
    @BindView(R.id.radio_group_horizontal)
    RadioGroup mRadioGroupHorizontal;
    @BindView(R.id.radio_group_vertical)
    RadioGroup mRadioGroupVertical;
    @BindView(R.id.seekbar_margin_h)
    SeekBar mSeekbarMarginH;
    @BindView(R.id.seekbar_margin_v)
    SeekBar mSeekbarMarginV;
    @BindView(R.id.seekbar_padding)
    SeekBar mSeekbarPadding;

    private BubblePopupWindow mBubblePopupWindow;
    private BubbleTextView mBubbleTextView;
    private RelativePos mRelativePos = new RelativePos(RelativePos.CENTER_HORIZONTAL, RelativePos.CENTER_VERTICAL);
    private int mMarginH = 0;
    private int mMarginV = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window_sample);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBubblePopupWindow != null) {
            mBubblePopupWindow.dismiss();
        }
    }

    private void initView() {
        @SuppressLint("InflateParams")
        View rootView = LayoutInflater.from(this).inflate(R.layout.simple_text_bubble, null);
        mBubbleTextView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
        mBubblePopupWindow = new BubblePopupWindow(rootView, mBubbleTextView);

        mSeekbarAnchorSideLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setAnchorViewSideLength(dp2px(progress + 20));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        setAnchorViewSideLength(dp2px(mSeekbarAnchorSideLength.getProgress() + 20));

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mBubbleTextView.setText(s.toString().trim());
            }
        });
        mBubbleTextView.setText(mEditText.getText().toString().trim());

        mCheckboxCancelOnTouch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBubblePopupWindow.setCancelOnTouch(isChecked);
                showPopupBubble();
            }
        });
        mCheckboxCancelOnTouch.setChecked(mCheckboxCancelOnTouch.isChecked());

        mCheckboxCancelOnTouchOutside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBubblePopupWindow.setCancelOnTouchOutside(isChecked);
                showPopupBubble();
            }
        });
        mBubblePopupWindow.setCancelOnTouchOutside(mCheckboxCancelOnTouchOutside.isChecked());

        mSeekbarAutoCancelTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBubblePopupWindow.setCancelOnLater(progress);
                showPopupBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mBubblePopupWindow.setCancelOnLater(mSeekbarAutoCancelTime.getProgress());

        mRadioGroupHorizontal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_center_horizontal:
                        mRelativePos.setHorizontalRelate(RelativePos.CENTER_HORIZONTAL);
                        break;
                    case R.id.radio_to_left_of:
                        mRelativePos.setHorizontalRelate(RelativePos.TO_LEFT_OF);
                        break;
                    case R.id.radio_to_right_of:
                        mRelativePos.setHorizontalRelate(RelativePos.TO_RIGHT_OF);
                        break;
                    case R.id.radio_align_left:
                        mRelativePos.setHorizontalRelate(RelativePos.ALIGN_LEFT);
                        break;
                    case R.id.radio_align_right:
                        mRelativePos.setHorizontalRelate(RelativePos.ALIGN_RIGHT);
                        break;
                }
                showPopupBubble();
            }
        });
        mRadioGroupHorizontal.check(R.id.radio_center_horizontal);

        mRadioGroupVertical.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_center_vertical:
                        mRelativePos.setVerticalRelate(RelativePos.CENTER_VERTICAL);
                        break;
                    case R.id.radio_above:
                        mRelativePos.setVerticalRelate(RelativePos.ABOVE);
                        break;
                    case R.id.radio_below:
                        mRelativePos.setVerticalRelate(RelativePos.BELOW);
                        break;
                    case R.id.radio_align_top:
                        mRelativePos.setVerticalRelate(RelativePos.ALIGN_TOP);
                        break;
                    case R.id.radio_align_bottom:
                        mRelativePos.setVerticalRelate(RelativePos.ALIGN_BOTTOM);
                        break;
                }
                showPopupBubble();
            }
        });
        mRadioGroupVertical.check(R.id.radio_center_vertical);

        mSeekbarMarginH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMarginH = dp2px(progress);
                showPopupBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mMarginH = dp2px(mSeekbarMarginH.getProgress());

        mSeekbarMarginV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMarginV = dp2px(progress);
                showPopupBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mMarginV = dp2px(mSeekbarMarginV.getProgress());

        mSeekbarPadding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBubblePopupWindow.setPadding(dp2px(progress));
                showPopupBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mBubblePopupWindow.setPadding(dp2px(mSeekbarPadding.getProgress()));

        mActivityPopupWindowSample.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            private int savedHeight = 0;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom != oldBottom && oldBottom != 0) {
                    ViewGroup.LayoutParams lp = mLayoutAnchor.getLayoutParams();
                    if (lp != null) {
                        if (bottom < oldBottom) {
                            savedHeight = lp.height;
                            lp.height = 0;
                            if (mBubblePopupWindow != null) {
                                mBubblePopupWindow.dismiss();
                            }
                        } else {
                            lp.height = savedHeight;
                        }
                        mLayoutAnchor.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    private void setAnchorViewSideLength(int length) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mAnchorView.getLayoutParams();
        if (lp == null) {
            lp = new RelativeLayout.LayoutParams(0, 0);
        }
        lp.width = length;
        lp.height = length;
        mAnchorView.setLayoutParams(lp);
        showPopupBubble();
    }

    private void showPopupBubble() {
        if (hasWindowFocus()) {
            mBubblePopupWindow.showArrowTo(mAnchorView, mRelativePos, mMarginH, mMarginV);
        }
    }

    @OnClick(R.id.view_anchor)
    public void onAnchorViewClick() {
        showPopupBubble();
    }
}
