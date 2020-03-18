package me.nereo.multi_image_selector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.bean.SelectBean;

/**
 * Multi image selector
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 * Updated by nereo on 2016/5/18.
 */
public class MultiImageSelectorActivity extends AppCompatActivity
        implements MultiImageSelectorFragment.Callback {

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    /**
     * Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * Select mode，{@link #MODE_MULTI} by default
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * Whether show camera，true by default
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * Result data set，ArrayList&lt;String&gt;
     */
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_RESULTS = "SELECT_RESULTS";
    /**
     * Original data set
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    public static final String EXTRA_DEFAULT_SELECTED_LIST_RETURN = "default_return_list";
    // Default image size
    private static final int DEFAULT_IMAGE_SIZE = 9;

//    private ArrayList<String> resultList = new ArrayList<>();
    private ArrayList<SelectBean> returnList = new ArrayList<>();
    private int selectNum = 0;
    private Button mSubmitButton;
    private int mDefaultCount = DEFAULT_IMAGE_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.mis_activity_default);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        final int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
//        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
//            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
//        }
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST_RETURN)) {

            returnList = intent.getParcelableArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST_RETURN);
            if (null != returnList && returnList.size()>0) {
                selectNum = returnList.size();
            }
        }

        mSubmitButton = (Button) findViewById(R.id.commit);
        if (mode == MODE_MULTI) {
            updateDoneText(returnList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (returnList != null && returnList.size() > 0) {
                        // Notify success
                        Intent data = new Intent();
                        data.putParcelableArrayListExtra(EXTRA_RESULTS, returnList);
//                        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                        setResult(RESULT_OK, data);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
//            bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
            bundle.putParcelableArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST,returnList);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update done button by select image data
     *
     * @param resultList selected image data
     */
    private void updateDoneText(ArrayList<SelectBean> resultList) {
        int size = 0;
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.mis_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            size = resultList.size();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setText(getString(R.string.mis_action_button_string,
                getString(R.string.mis_action_done), size, mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
//        resultList.add(selectBean);

        SelectBean bean = new SelectBean(path, selectNum);
        returnList.add(bean);
        selectNum++;
        data.putParcelableArrayListExtra(EXTRA_RESULTS, returnList);
//        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onImageSelected(String path) {
        Log.i("MultiImageSelectorActivity", "onImageSelected: path:" + path);
//        if (!resultList.contains(path)) {
//            resultList.add(path);
//        }

        if (null != returnList && returnList.size()>0) {
            boolean isExit = false;
            for (SelectBean bean : returnList) {
                if (bean.getPath().equals(path)) {
                   isExit = true;
                   break;
                }
            }

            if (!isExit){

                SelectBean beanSelect = new SelectBean(path, selectNum);
                returnList.add(beanSelect);
                selectNum++;
            }

        }else {
            returnList.add(new SelectBean(path,selectNum));
            selectNum++;
        }

//updateDoneText(resultList);
        updateDoneText(returnList);
    }

    @Override
    public void onImageUnselected(String path) {
//        if (resultList.contains(path)) {
//            resultList.remove(path);
//        }
        SelectBean selectBean = null;
        for (SelectBean bean : returnList) {
            boolean equals = bean.getPath().equals(path);
            if (equals) {
                selectBean = bean;
            }
        }

        if (null != selectBean) {
            returnList.remove(selectBean);
        }

        ArrayList<SelectBean> selectBeanArrayList = new ArrayList<>();
        if (returnList.size()>0) {
            selectBeanArrayList.addAll(returnList);
            returnList.clear();
            for (int i = 0; i < selectBeanArrayList.size(); i++) {
                SelectBean selectBean1 = selectBeanArrayList.get(i);
                selectBean1.setPosition(i);
                returnList.add(selectBean1);
            }
        }

//        updateDoneText(resultList);
        updateDoneText(returnList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            Log.i("MultimageSelector", "onCameraShot: imageFile："+imageFile);
            // notify system the image has change
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
//            resultList.add(imageFile.getAbsolutePath());


            SelectBean bean = new SelectBean(imageFile.getAbsolutePath(), selectNum);
            bean.setPath(imageFile.getAbsolutePath());
            boolean contains = returnList.contains(bean);
            if (!contains) {
                returnList.add(bean);
                selectNum++;
            }
            data.putParcelableArrayListExtra(EXTRA_RESULTS, returnList);
//            data.putStringArrayListExtra(EXTRA_RESULT, resultList);

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
