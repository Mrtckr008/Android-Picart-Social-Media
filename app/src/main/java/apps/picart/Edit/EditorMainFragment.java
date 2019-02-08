package apps.picart.Edit;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import apps.picart.Filters.FiltersListFragment;
import apps.picart.R;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */


/* Bu fragmentin tek amacı diğer edit fragmentlarını toparlayıp  FiltersActivity'deki viewpager içinde görsel olarak sunmak
     ve o görselden hangi imageviewa tıklanınca hangi fonksiyonun çalışacağını belirlemek. */

public class EditorMainFragment extends Fragment  {

    @BindView(R.id.imgPencil) ImageView imgPencil;

    @BindView(R.id.btnEraser) ImageView imgEraser;

    @BindView(R.id.imgSticker) ImageView imgSticker;

    @BindView(R.id.imgEmoji) ImageView imgEmo;

    @BindView(R.id.imgText) ImageView imgText;




    EditorMainClickListener listenerClick;

    public void setListener(EditorMainClickListener listener) {
        this.listenerClick = listener;
    }

    public EditorMainFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_editor_main, null);
        ButterKnife.bind(this,view);

        imgEmo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                listenerClick.onImgEmojiClick();

            }
        });
        imgSticker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                listenerClick.onImgStickerClick();

            }
        });
        imgPencil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                listenerClick.onImgPencilClick();

            }
        });
        imgEraser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                listenerClick.onImgEraserClick();

            }
        });

        imgText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                listenerClick.onImgTxtClick();

            }
        });



        return view;

    }



    //NOT : VİEWPAGE'DEKİ BUTONLAR BU FRAGMENTTA OLDUGUNDAN ANA FİLTER ACTİVİTYE DÖNÜŞ İÇİN YAZDIM !....
    public interface EditorMainClickListener {

        void onImgStickerClick();

        void onImgEmojiClick();

        void onImgEraserClick();

        void onImgPencilClick();

        void onImgTxtClick();

    }

}

