package com.soil.profile.model.CustomChooseMdl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.soil.profile.model.BaseProfileModel;
import com.soil.profile.ui.ActivityEditPhoto;
import com.soil.profile.utils.DrawLegend;
import com.soil.soilsample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SixLayerCusMdl extends BaseProfileModel implements OnGestureListener{
    private int[] proLegendCodes;
    private String[] layerNames;
    private float[] depths;
    private float sumDepth;
    private boolean showDepth = true;

    private Context context;
    private GestureDetector mDetector;
    int gap = 10;
    int legend;
    int lynum;

    @SuppressLint("ClickableViewAccessibility")
    public SixLayerCusMdl(Context context, float[] lines, String[] layerName, float sumDepth){
        super(context, lines);
        this.context = context;

        this.layerNames = layerName;
        this.sumDepth = sumDepth;
        depths = new float[lines.length-1];
        for (int i = 0; i < depths.length; i++) {
            depths[i] = sumDepth*(lines[i+1]-lines[i]);
        }

        mDetector = new GestureDetector(context, this);
        setLongClickable(true);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

    public int[] getPro_name() {
        return proLegendCodes;
    }

    public void setPro_name(int[] pro_name) {
        this.proLegendCodes = new int[pro_name.length];
        for (int i = 0; i < pro_name.length; i++) {
            this.proLegendCodes[i] = pro_name[i];
        }
    }
    @SuppressLint({ "UseValueOf", "ClickableViewAccessibility" })
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int gap = 10;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showDepth = false;
                if (fmlLines[4]- gap < event.getY() && event.getY() < fmlLines[4] + gap) {
                    flag = 4;
                }else if (fmlLines[3]-gap < event.getY() && event.getY() < fmlLines[3] + gap) {
                    flag = 3;
                }else if (fmlLines[2]-gap < event.getY() && event.getY() < fmlLines[2] + gap) {
                    flag = 2;
                }else if (fmlLines[1]-gap < event.getY() && event.getY() < fmlLines[1] + gap){
                    flag = 1;
                }else if (fmlLines[5]-gap < event.getY() && event.getY() < fmlLines[5] + gap){
                    flag = 5;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                showDepth = false;
                switch (flag) {
                    case 5:
                        if (event.getY()>fmlLines[4]+20 && event.getY()<1038) {
                            fmlLines[5] = event.getY();
                            invalidate();
                        }
                    case 4:
                        if (event.getY()> fmlLines[3]+20 && event.getY()<fmlLines[5]-20) {
                            fmlLines[4] = event.getY();
                            invalidate(); //重新绘制区域
                        }
                        break;
                    case 3:
                        if (event.getY()> fmlLines[2]+20 && event.getY()<fmlLines[4]-20) {
                            fmlLines[3] = event.getY();
                            invalidate(); //重新绘制区域
                        }
                        break;
                    case 2:
                        if (event.getY()> fmlLines[1]+20 && event.getY()<fmlLines[3]-20) {
                            fmlLines[2] = event.getY();
                            invalidate(); //重新绘制区域
                        }
                        break;
                    case 1:
                        if (event.getY()> 20 && event.getY()<fmlLines[2]-20) {
                            fmlLines[1] = event.getY();
                            invalidate(); //重新绘制区域
                        }
                        break;
                }
                break;

            case MotionEvent.ACTION_UP:
                showDepth = true;
//			lynum = 0;
                flag = 0;
                break;
        }
        return true;
    }

    @SuppressLint("DrawAllocation") @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = canvas.getHeight();

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasBitmap = new Canvas(bitmap);

        setImageResource(R.drawable.transparent_backgroud_frame);

        for (int i = 0; i < proLegendCodes.length; i++) {
            if(0 == proLegendCodes[i])
                break;
            if (isDraw[i]) {
                DrawLegend.drawCustomChoose(proLegendCodes[i], canvasBitmap, paint, linePaint, width, fmlLines[i], fmlLines[i+1], layerNames[i]);
                if (showDepth) {
                    modifyDepth();
                    DrawLegend.drawDepth(canvasBitmap, paint, linePaint, width, fmlLines[i], fmlLines[i+1], depths[i]);
                }
            }
        }

        DrawLegend.drawSpilitLine(canvasBitmap, paint, width, height);
        canvas.drawBitmap(bitmap, 0, 0 , paint);
    }

    private void modifyDepth(){
        for (int i = 0; i < depths.length; i++) {
            depths[i] = sumDepth*(fmlLines[i+1]-fmlLines[i])/HEIGHT;
        }
    }

    public List<Map<String, Object>> getLayerInfo(){
        List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name",layerNames[i]);
            map.put("length", depths[i]);
            infoList.add(map);
        }
        return infoList;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if (event.getY()> fmlLines[5]+20 && event.getY()<950) {
            lynum = 6;
        }else if (event.getY()> fmlLines[4]+20 && event.getY()<fmlLines[5]-20) {
            lynum = 5;
        }else if (event.getY()> fmlLines[3]+20 && event.getY()<fmlLines[4]-20) {
            lynum = 4;
        }else if (event.getY()> fmlLines[2]+20 && event.getY()<fmlLines[3]-20) {
            lynum = 3;
        }else if (event.getY()> fmlLines[1]+20 && event.getY()<fmlLines[2]-20) {
            lynum = 2;
        }else if (event.getY()> 20 && event.getY()<fmlLines[1]-20){
            lynum = 1;
        }
        if (lynum>0) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
            builder.setTitle("更改图例"); //设置标题
            Spinner spinner = new Spinner(getContext());
            SimpleAdapter adapter = new SimpleAdapter(getContext(), ActivityEditPhoto.itemList, R.layout.item_cus_chs_mdl,
                    new String[]{"pic"}, new int[]{R.id.id_cus_item_pic});
            adapter.setDropDownViewResource(R.layout.item_cus_chs_mdl);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                    legend = ( position==0 )?0:DrawLegend.proLegengCode[position-1];
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO 自动生成的方法存根

                }
            });
            builder.setView(spinner);
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //关闭dialog
                    proLegendCodes[lynum-1] = legend;
                    invalidate();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() { //设置取消按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            //参数都设置完成了，创建并显示出来
            builder.create().show();
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO 自动生成的方法存根

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO 自动生成的方法存根
        return false;
    }

    public int[] getProLegendCodes() {
        return proLegendCodes;
    }

    public void setProLegendCodes(int[] proLegendCodes) {
        this.proLegendCodes = proLegendCodes;
    }

    public String[] getLayerNames() {
        return layerNames;
    }

    public void setLayerNames(String[] layerNames) {
        this.layerNames = layerNames;
    }

    public float[] getDepths() {
        return depths;
    }

    public void setDepths(float[] depths) {
        this.depths = depths;
    }
}

