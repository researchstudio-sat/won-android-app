package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import at.researchstudio.sat.won.android.won_android_app.app.R;

/**
 * Created by fsuda on 21.08.2014.
 */
public class MailboxFragment extends Fragment {
    private Button cameraButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_mailbox, container, false);

        cameraButton = (Button) rootView.findViewById(R.id.camera);

        cameraButton.setOnClickListener(new Button.OnClickListener() {


                                            public void onClick(View v) {
                                                if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                                    }
                                                }else{
                                                    //OJE :-(
                                                }
                                            }
                                        });

        getActivity().setTitle(R.string.mi_mailbox);
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView mImageView = (ImageView) getActivity().findViewById(R.id.imageView1);
            mImageView.setImageBitmap(imageBitmap);
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}
