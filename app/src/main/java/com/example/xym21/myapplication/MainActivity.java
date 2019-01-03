package com.example.xym21.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
public class MainActivity extends AppCompatActivity {

    //   获取头像

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private static final String CROP_IMAGE_FILE_NAME = "bala_crop.jpg";
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    public static boolean clickfight=false;
    //改变头像的标记位
    private int new_icon=0xa3;
    private ImageView headImage = null;
    private String mExtStorDir;
    private Uri mUriPath;

    private final int PERMISSION_READ_AND_CAMERA =0;//读和相机权限
    private final int PERMISSION_READ =1;//读取权限



    //获取头像
    private ProgressBar pb1;
    private ProgressBar pb2;
    private ProgressBar pb3;
    private ProgressBar pblevel;
    private TextView tv_main_life;
    private TextView tv_main_atk;
    private TextView tv_main_speed;
    private TextView main_tv_1;
    private TextView tv_main_level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示 username
        Intent intent1=getIntent();
        Bundle bundle=intent1.getExtras();//.getExtras()得到 intent 所附带的额外数据
        String str=bundle.getString("username");//getString()返回指定 key 的值
        main_tv_1=(TextView) findViewById(R.id.main_tv_1);//用 TextView 显示值
        main_tv_1.setText(str+"的乔巴酱");
        //(1)找到我们关心的控件
        pb1 = (ProgressBar) findViewById(R.id.progressBar1);
        pb2 = (ProgressBar) findViewById(R.id.progressBar2);
        pb3 = (ProgressBar) findViewById(R.id.progressBar3);
        pblevel =(ProgressBar)findViewById(R.id.progressBar4);
        tv_main_life = (TextView) findViewById(R.id.tv_main_life);
        tv_main_atk = (TextView) findViewById(R.id.tv_main_atk);
        tv_main_speed = (TextView) findViewById(R.id.tv_main_speed);
        tv_main_level = (TextView) findViewById(R.id.tv_main_level);
        //(2)初始化进度条的最大值
        pb1.setMax(500);
        pb2.setMax(200);
        pb3.setMax(200);
        pblevel.setMax(3);



        //头像
        //头像
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        headImage = (ImageView) findViewById(R.id.imageViewspec);

        Button buttonLocal = (Button) findViewById(R.id.buttonLocal);
        buttonLocal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                choseHeadImageFromGallery();
                checkReadPermission();

            }
        });

        Button buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                choseHeadImageFromCameraCapture();
                checkStoragePermission();//检查是否有权限
            }
        });
    }
    public void click(View v) {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivityForResult(intent, 1);
    }
    public void click_f(View v) {
        if(!clickfight){
        Intent intent = new Intent(this, fightActivity.class);
        startActivityForResult(intent, 1);clickfight=true;
            Toast.makeText(this, "在这十秒钟之内，尽情输出吧", Toast.LENGTH_SHORT).show();}
        else{Toast.makeText(this, "生命只有一次，且行且珍惜", Toast.LENGTH_SHORT).show();}

    }

    private void updateProgressBar(ItemInfo info) {
        //(1)获取当前 progressbar 的进展
        int progress1 = pb1.getProgress();
        int progress2 = pb2.getProgress();
        int progress3 = pb3.getProgress();
        int progress4 = pblevel.getProgress();
        //(2)更新一下 progressbar 的进展
        int gatenum=progress1+info.getLife();
        if(gatenum>=0){
        pb1.setProgress(progress1 + info.getLife());
        pb2.setProgress(progress2 + info.getAcctack());
        pb3.setProgress(progress3 + info.getSpeed());}
        else{Toast.makeText(this, "Not Enough Exp", Toast.LENGTH_SHORT).show();}
        //更新一下 textview 的值
        if(pb2.getProgress()>150 && pb3.getProgress()>150){pblevel.setProgress(3);Toast.makeText(this, "Master！受我一拜！", Toast.LENGTH_SHORT).show();}
        else if(pb2.getProgress()>100 && pb3.getProgress()>100){pblevel.setProgress(2);}
        else if(pb2.getProgress()>50 && pb3.getProgress()>50){pblevel.setProgress(1);}
        tv_main_life.setText(pb1.getProgress() + "");
        tv_main_atk.setText(pb2.getProgress() + "");
        tv_main_speed.setText(pb3.getProgress() + "");
        tv_main_level.setText(pblevel.getProgress() + "");
    }


    //头像
    //头像
    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        // 设置文件类型    （在华为手机中不能获取图片，要替换代码）
        /*Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image*//*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);*/

        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);

    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        String savePath = mExtStorDir;

        Intent intent = null;
        // 判断存储卡是否可以用，可用进行存储

        if (hasSdcard()) {
            //设定拍照存放到自己指定的目录,可以先建好
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            Uri pictureUri;
            File pictureFile = new File(savePath, IMAGE_FILE_NAME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                pictureUri = FileProvider.getUriForFile(this, getPackageName()+".fileProvider", pictureFile);
    /*ContentValues contentValues = new ContentValues(1);
    contentValues.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());
    pictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);*/
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureUri = Uri.fromFile(pictureFile);
            }
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());
                pictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pictureUri = Uri.fromFile(pictureFile);
            }*/
            if (intent != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        pictureUri);
                startActivityForResult(intent, CODE_CAMERA_REQUEST);
            }
        }
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    //当我们开启的 Activity 关闭的时候调用这个方法

    //更新当前控件的 UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(resultCode==10){
        //(1)代表数据来源于 shoppingActivity 取数据
  ItemInfo info = (ItemInfo) intent.getExtras().get("info");
        //（2）更新一下 UI
 updateProgressBar(info);}

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
//                    cropRawPhoto(Uri.fromFile(tempFile));
                    cropRawPhoto(getImageContentUri(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;

            case CODE_RESULT_REQUEST:
                /*if (intent != null) {
                    setImageToHeadView(intent);    //此代码在小米有异常，换以下代码
                }*/
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUriPath));
                    setImageToHeadView(intent,bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void checkStoragePermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(pblevel.getProgress()<=1){Toast.makeText(this, "再接再厉!!", Toast.LENGTH_SHORT).show();}
        else{if (result == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {/*Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,*/Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ_AND_CAMERA);
        } else {
            choseHeadImageFromCameraCapture();
        }}
    }


    private void checkReadPermission() {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(pblevel.getProgress()<=0){Toast.makeText(this, "努力培养吧!!", Toast.LENGTH_SHORT).show();}
        else{
        if (permission==PackageManager.PERMISSION_DENIED){
            String[] permissions ={Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_READ);
        }else {
            choseHeadImageFromGallery();
        }}

    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_READ_AND_CAMERA:
                for (int i=0;i<grantResults.length;i++){
                    if (grantResults[i]==PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "why ??????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                choseHeadImageFromCameraCapture();
                break;
            case PERMISSION_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choseHeadImageFromGallery();
                }
                break;
        }

    }
    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        //startActivityForResult(intent, CODE_RESULT_REQUEST); //直接调用此代码在小米手机有异常，换以下代码
        String mLinshi = System.currentTimeMillis() + CROP_IMAGE_FILE_NAME;
        File mFile = new File(mExtStorDir, mLinshi);
//        mHeadCachePath = mHeadCacheFile.getAbsolutePath();

        mUriPath = Uri.parse("file://" + mFile.getAbsolutePath());
        //将裁剪好的图输出到所建文件中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPath);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //注意：此处应设置return-data为false，如果设置为true，是直接返回bitmap格式的数据，耗费内存。设置为false，然后，设置裁剪完之后保存的路径，即：intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
//        intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent,Bitmap b) {
        /*Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            headImage.setImageBitmap(photo);
        }*/
        try {
            if (intent != null) {

             Bitmap bitmap = imageZoom(b);//看个人需求，可以不压缩
                headImage.setImageBitmap(b);
//                long millis = System.currentTimeMillis();
                /*File file = FileUtil.saveFile(mExtStorDir, millis+CROP_IMAGE_FILE_NAME, bitmap);
                if (file!=null){
                    //传递新的头像信息给我的界面
                    Intent ii = new Intent();
                    setResult(new_icon,ii);
                    Glide.with(this).load(file).apply(RequestOptions.circleCropTransform())
//                                .apply(RequestOptions.fitCenterTransform())
                            .apply(RequestOptions.placeholderOf(R.mipmap.user_logo)).apply(RequestOptions.errorOf(R.mipmap.user_logo))
                            .into(mIvTouxiangPersonal);
//                uploadImg(mExtStorDir,millis+CROP_IMAGE_FILE_NAME);
                    uploadImg(mExtStorDir,millis+CROP_IMAGE_FILE_NAME);
                }*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    private Bitmap imageZoom(Bitmap bitMap) {
        //图片允许最大空间   单位：KB
        double maxSize =100.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
