package com.dlut.mnist.scriptrecognizer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.dlut.mnist.scriptrecognizer.DAO.DataBean;
import com.dlut.mnist.scriptrecognizer.DAO.DataManager;

public class DataListAdapter extends BaseAdapter {
    private String TAG = "MyAdapter";
    private ViewHolder viewholder;
    private Context mctx;
    private int count;

    public DataListAdapter(Context mctx) {
        this.mctx = mctx;
    }

    @Override
    public int getCount() {
        count = DataManager.getInstance().getDataCount();

        return count + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //传入三个参数，关注前两个
        //  第一个为位置索引
        //  第二个为“回收站”，可以判断是否利用可以回收利用的View，节约内存需要使用

        if (view == null) {
            view = View.inflate(mctx, R.layout.info_item, null);
            viewholder = new ViewHolder(
                    (EditText) view.findViewById(R.id.et_name),
                    (EditText) view.findViewById(R.id.et_stunum),
                    (EditText) view.findViewById(R.id.et_score));
            view.setTag(viewholder);


        } else {
            viewholder = (ViewHolder) view.getTag();
        }
        //  对ViewHolder进行更新
        if (i != count) {
            DataBean data = DataManager.getInstance().getDataByOrder(i);
            viewholder.et_score.setHint("Score");
            viewholder.et_score.setText(DataManager.getInstance().getScore(i));
            viewholder.et_name.setText(data.getName());
            viewholder.et_name.setTextColor(Color.BLACK);
            viewholder.et_stunum.setText(data.getStunum());
            viewholder.et_stunum.setTextColor(Color.BLACK);
            viewholder.et_name.setOnLongClickListener(v -> {
                Log.d(TAG, "onLongClick: intro the LONG PRESS");
                v.setFocusableInTouchMode(true);
                v.setBackgroundColor(Color.LTGRAY);

                v.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, 0);
                ((EditText) v).setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //处理事件
                        Log.d(TAG, "onEditorAction: 处理完毕！");
                        v1.setFocusableInTouchMode(false);
                        v.setBackgroundColor(Color.TRANSPARENT);

                        v1.clearFocus();

                        //  写入新的姓名
                        ViewParent view1 = v.getParent();
                        Log.d(TAG, "getView: view1-----"+view1);
                        ViewParent view2 = view1.getParent();
                        Log.d(TAG, "getView: view2-----"+view2);
                        int positon = ((ListView) view2).getPositionForView((View) view1);
                        Log.d(TAG, "getView: "+positon);

                        String string = v1.getText().toString().trim();
                        DataManager.getInstance().changeName(positon, string);
                    }
                    return false;
                });

                return true;
            });
            viewholder.et_stunum.setOnLongClickListener(v -> {
                Log.d(TAG, "onLongClick: intro the LONG PRESS");
                v.setFocusableInTouchMode(true);
                v.setBackgroundColor(Color.LTGRAY);
                v.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, 0);
                ((EditText) v).setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //处理事件
                        Log.d(TAG, "onEditorAction: 处理完毕！");
                        v1.setFocusableInTouchMode(false);
                        v.setBackgroundColor(Color.TRANSPARENT);
                        v1.clearFocus();

                        //  写入新的姓名
                        ViewParent view1 = v.getParent();
                        Log.d(TAG, "getView: view1-----"+view1);
                        ViewParent view2 = view1.getParent();
                        Log.d(TAG, "getView: view2-----"+view2);
                        int positon = ((ListView) view2).getPositionForView((View) view1);
                        Log.d(TAG, "getView: "+positon);

                        String string = v1.getText().toString().trim();
                        DataManager.getInstance().changeStunum(positon, string);
                    }
                    return false;
                });

                return true;
            });
            viewholder.et_score.setFocusableInTouchMode(true);
            viewholder.et_score.setFocusable(true);

            viewholder.et_score.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    //  写入新的姓名
                    ViewParent view1 = v.getParent();
                    Log.d(TAG, "getView: view1-----"+view1);
                    ViewParent view2 = view1.getParent();
                    Log.d(TAG, "getView: view2-----"+view2);
                    int positon = ((ListView) view2).getPositionForView((View) view1);
                    Log.d(TAG, "getView: "+positon);

                    String string = v.getText().toString().trim();
                    DataManager.getInstance().addScore(positon, string);
                }
                return false;
            });
        } else {
            viewholder.et_name.setText("添加");
            viewholder.et_name.setTextColor(Color.LTGRAY);
            viewholder.et_name.setOnLongClickListener(v -> {
                Log.d(TAG, "onLongClick: intro the LONG PRESS");
                v.setFocusableInTouchMode(true);
                v.setBackgroundColor(Color.LTGRAY);
                ((EditText)v).setText("");
                ((EditText)v).setTextColor(Color.BLACK);

                v.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, 0);
                ((EditText) v).setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //处理事件
                        Log.d(TAG, "onEditorAction: 处理完毕！");
                        v1.setFocusableInTouchMode(false);
                        v.setBackgroundColor(Color.TRANSPARENT);

                        v1.clearFocus();

                        DataManager.getInstance().addNewByName(v1.getText().toString().trim());

                        ViewParent view1 = v.getParent();
                        Log.d(TAG, "getView: view1-----"+view1);
                        ViewParent view2 = view1.getParent();
                        Log.d(TAG, "getView: view2-----"+view2);
                        ViewUtils.runOnUiThread(() -> {
                            ListAdapter adapter = ((ListView) view2).getAdapter();
                            ((DataListAdapter)adapter).notifyDataSetChanged();
                        });
                    }
                    return false;
                });

                return true;
            });

            viewholder.et_stunum.setText("添加");
            viewholder.et_stunum.setTextColor(Color.LTGRAY);

            viewholder.et_stunum.setOnLongClickListener(v -> {
                Log.d(TAG, "onLongClick: intro the LONG PRESS");
                v.setFocusableInTouchMode(true);
                v.setBackgroundColor(Color.LTGRAY);
                ((EditText)v).setText("");
                ((EditText)v).setTextColor(Color.BLACK);

                v.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, 0);
                ((EditText) v).setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //处理事件
                        Log.d(TAG, "onEditorAction: 处理完毕！");
                        v1.setFocusableInTouchMode(false);
                        v.setBackgroundColor(Color.TRANSPARENT);
                        v1.clearFocus();

                        DataManager.getInstance().addNewByStunum(v1.getText().toString().trim());
                        ViewParent view1 = v.getParent();
                        Log.d(TAG, "getView: view1-----"+view1);
                        ViewParent view2 = view1.getParent();
                        Log.d(TAG, "getView: view2-----"+view2);
                        ViewUtils.runOnUiThread(() -> {
                            ListAdapter adapter = ((ListView) view2).getAdapter();
                            ((DataListAdapter)adapter).notifyDataSetChanged();
                        });
                    }
                    return false;
                });

                return true;
            });

            viewholder.et_score.setHint("");
            viewholder.et_score.setFocusableInTouchMode(false);

        }


        return view;
    }

    private class ViewHolder {
        EditText et_name;
        EditText et_stunum;
        EditText et_score;

        ViewHolder(EditText et_name, EditText et_stunum, EditText et_score) {
            this.et_name = et_name;
            this.et_stunum = et_stunum;
            this.et_score = et_score;
        }

    }
}
