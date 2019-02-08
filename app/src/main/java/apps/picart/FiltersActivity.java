package apps.picart;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import apps.picart.Edit.EditorMainFragment;
import apps.picart.Edit.EmojiBSFragment;
import apps.picart.Edit.PropertiesBSFragment;
import apps.picart.Edit.Rotate_Crop_Fragment;
import apps.picart.Edit.StickerBSFragment;
import apps.picart.Edit.TextEditorDialogFragment;
import apps.picart.Filters.EditImageFragment;
import apps.picart.Filters.FiltersListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import apps.picart.utils.*;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import static apps.picart.utils.BitmapUtils.decodeSampledBitmapFromResource;
import static apps.picart.utils.BitmapUtils.getBitmapFromAssets;
import static apps.picart.utils.BitmapUtils.getImageBitmapFromImageView;
import static apps.picart.utils.BitmapUtils.getImageUrifromBitmap;
import static maes.tech.intentanim.CustomIntent.customType;

public class FiltersActivity extends BaseActivity implements
        FiltersListFragment.FiltersListFragmentListener,
        EditImageFragment.EditImageFragmentListener, OnPhotoEditorListener,
        EditorMainFragment.EditorMainClickListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener,
        Rotate_Crop_Fragment.RotateCropListener

{



    private static final String TAG = FiltersActivity.class.getSimpleName();

    public static final String IMAGE_NAME = "imgefect.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;
    public static final int REQUEST_CAMERA = 100;
    @BindView(R.id.image_preview)
    PhotoEditorView mPhotoEditorView;

    Uri ImageUri;

    ImageView imagePreview;
    @BindView(R.id.btn_clearall)
    ImageButton btn_clearall;

    @BindView(R.id.imgUndo)
    ImageButton btn_undo;

    @BindView(R.id.imgRedo)
    ImageButton btn_redo;
    @BindView(R.id.tabs) TabLayout tabLayout;

    @BindView(R.id.viewpager) ViewPager viewPager;

    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;


    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";

    private PhotoEditor mPhotoEditor;


    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private EditorMainFragment mEditorMainFragment;
    private Rotate_Crop_Fragment mRotate_crop_fragment;
    final int PIC_CROP = 1;

    Typeface mTextRobotoTf;

    //  native image filter kütüphanesi yükleniyor.
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));
        imagePreview=new ImageView(this);
        loadImage();

        mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

        setPhotoEditorViewSouce(imagePreview);

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mPhotoEditor.setOnPhotoEditorListener(this);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);



        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();

            }
        });

        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();

            }
        });

        btn_clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetControls();
                mPhotoEditor.clearAllViews();
                imagePreview.setImageBitmap(originalImage);
                setPhotoEditorViewSouce(imagePreview);

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        //Adding editor Fragment
        mEditorMainFragment = new EditorMainFragment();
        mEditorMainFragment.setListener(this);

        //Adding Settings Fragment
        mRotate_crop_fragment = new Rotate_Crop_Fragment();
        mRotate_crop_fragment.setListener(this);



        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));
        adapter.addFragment(mEditorMainFragment,"Edit");
        adapter.addFragment(mRotate_crop_fragment,"Ayarlar");


        viewPager.setAdapter(adapter);

    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        setPhotoEditorViewSouce(imagePreview);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

        setPhotoEditorViewSouce(imagePreview);

    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));


        setPhotoEditorViewSouce(imagePreview);
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

        setPhotoEditorViewSouce(imagePreview);

    }

    @Override
    public void onEditStarted() {

    }


    //hatali calısıyor ton kısmı!
    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);

//        Bitmap bitmap=((BitmapDrawable)imagePreview.getDrawable()).getBitmap();
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .setDefaultTextTypeface(mTextRobotoTf)
//                   .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }


    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onImgStickerClick() {

        mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
    }

    @Override
    public void onImgEmojiClick() {
        mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());

    }

    @Override
    public void onImgEraserClick() {
        mPhotoEditor.brushEraser();
    }

    @Override
    public void onImgPencilClick() {

        mPhotoEditor.setBrushDrawingMode(true);
        mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());

    }

    @Override
    public void onImgTxtClick() {

        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.addText(inputText, colorCode);
            }
        });





    }


    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);


//        finalImage = mPhotoEditorView.getRootView().getDrawingCache(true);


    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);


//        finalImage = mPhotoEditorView.getRootView().getDrawingCache(true);

    }






    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {


        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
            }
        });

//        finalImage = mPhotoEditorView.getRootView().getDrawingCache(true);


    }

    @Override
    public void onImgRotateClick() {


        Bitmap myImg = getImageBitmapFromImageView(imagePreview);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);
        finalImage = rotated.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(rotated);
        setPhotoEditorViewSouce(imagePreview);



    }

    @Override
    public void onImgCropClick() {


        ImageUri= getImageUrifromBitmap(this,getImageBitmapFromImageView(imagePreview));
        CropImage(ImageUri); // KIRPMA KISMI (GALERİDEN VEYA KAMERADAN MUTLAKA ALMALI İMAGE'İ


    }





    // load the default image from assets on app launch
    private void loadImage() {

        originalImage = getBitmapFromAssets(this,  IMAGE_NAME, mPhotoEditorView.getMeasuredWidth(),  mPhotoEditorView.getMeasuredHeight());
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);

        ImageUri= getImageUrifromBitmap(this,originalImage);



//        filtersListFragment.prepareThumbnail(originalImage);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.open) {
//            openImageFromGallery();
            showDialogforresource();
            return true;
        }
        if (id == android.R.id.home) {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            customType(FiltersActivity.this,"right-to-left");

            finish();
            return true;
        }
        if (id == R.id.save) {
            saveImageToGallery();
            return true;
        }

        if (id == R.id.action_post) {


            mPhotoEditorView.setDrawingCacheEnabled(true);
            finalImage= mPhotoEditorView.getDrawingCache();



            BitmapHelper.getInstance().setBitmap(finalImage);

            if(BitmapHelper.getInstance().getBitmap() == null)
                Toast.makeText(this,"null",Toast.LENGTH_LONG);
            else {
                Intent intent = new Intent();
                intent.setClass(this, PostActivity.class);
                startActivity(intent);

                customType(FiltersActivity.this,"left-to-right");

                // finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(FiltersActivity.this,"right-to-left");


    }
    private void showDialogforresource(){

        AlertDialog.Builder builder = new AlertDialog.Builder(FiltersActivity.this);
        builder.setTitle("Uploading Images");
        builder.setMessage("Upload Your Image Resource From:");
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

                //İptal butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , SELECT_GALLERY_IMAGE);
            }
        });

        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //CAMERA butonuna basılınca yapılacaklar
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CAMERA);

            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            try {
                originalImage.recycle();
                finalImage.recycle();
                finalImage.recycle();
            }catch (Exception e){

            }

            ImageUri = data.getData(); // CROP METHODU İÇİN!

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);

            imagePreview.setImageBitmap(originalImage);

            setPhotoEditorViewSouce(imagePreview);
            bitmap.recycle();

            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);

        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {
//            Bitmap bitmap =   onCaptureImageResult(data);
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 1000, 1000);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            ImageUri = data.getData(); // CROP METHODU İÇİN!

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);

            setPhotoEditorViewSouce(imagePreview);
            bitmap.recycle();

            // render selected image thumbnails >>>>>>>>>>>>>>>>>>>>>>> DÜZENLENECEK(KAMERADAN VEYA GALERİDEN ALINAN FOTOLARIN
            // KIRPMA KISMINA GECİP GERİ GELDİKTEN SONRA THUMNAILLARI SİLİNİYOR!
            filtersListFragment.prepareThumbnail(originalImage);

        }
        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");

                originalImage = selectedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalImage = selectedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                imagePreview.setImageBitmap(selectedBitmap);
                setPhotoEditorViewSouce(imagePreview);

                filtersListFragment.prepareThumbnail(originalImage);

            }
        }
        /*
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle b = new Bundle();
        b.putByteArray("image",byteArray);  */


        // your fragment code
     //   filtersListFragment.setArguments(b);
    }

    //Relativelayout view'inin source'unu ayarlamak için kullandım.
    private void setPhotoEditorViewSouce(ImageView image){
        Bitmap bitmap2=((BitmapDrawable)image.getDrawable()).getBitmap();

        mPhotoEditorView.getSource().setImageBitmap(bitmap2);




        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .setDefaultTextTypeface(mTextRobotoTf)
                .build(); // build photo editor sdk



    }

    private void openImageFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);

//                            Intent intent = new Intent(Intent.ACTION_PICK);
//                            intent.setType("image/*");
//                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
    * saves image to camera gallery
    * */
    private void saveImageToGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Galeriye Kaydedildi !", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);
                                            }
                                        });

                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Başarısız!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Başarısız! İzinleri Kontrol ediniz..", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void CropImage(Uri picUri) { //GALERİ VEYA KAMERADAN IMAGE ALMAZSA ÇALIŞMAZ !
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
