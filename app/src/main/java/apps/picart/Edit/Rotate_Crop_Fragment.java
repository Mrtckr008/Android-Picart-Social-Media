package apps.picart.Edit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import apps.picart.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Rotate_Crop_Fragment extends Fragment {


    @BindView(R.id.imgRotateLeft)
    ImageView imgRotateleft;

    @BindView(R.id.imgCrop)
    ImageView imgCrop;

    RotateCropListener listener;


    public Rotate_Crop_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_rotate_crop, null);
        ButterKnife.bind(this,view);
        imgRotateleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImgRotateClick();
            }
        });

        imgCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImgCropClick();
            }
        });


        return view;

    }


    public void setListener(RotateCropListener rotateCropListener) {

    listener = rotateCropListener;
    }




    //NOT : VİEWPAGE'DEKİ BUTONLAR BU FRAGMENTTA OLDUGUNDAN ANA FİLTER ACTİVİTYE DÖNÜŞ İÇİN YAZDIM !....
    public interface RotateCropListener {

        void onImgRotateClick();

        void onImgCropClick();

    }




}
