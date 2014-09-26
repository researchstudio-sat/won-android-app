package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;

import java.io.InputStream;

/**
 * Created by fsuda on 25.08.2014.
 */
public class ImageFragment extends Fragment {
    public static final String ARG_IMAGE_URL = "image_url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        String imageUrl;

        Bundle args = getArguments();

        ImageView welcomeImage = (ImageView) rootView.findViewById(R.id.create_image);
        //TODO: REMOVE THIS LOADING THINGY THIS DOES NOT DO ANYTHING RIGHT
        if(args!=null) {
            imageUrl = args.getString(ARG_IMAGE_URL);
            Log.d("ImageFragment", "ImageUrl: "+imageUrl);

            switch ((int)(Math.random()*100)%3) {
                case 0:
                default:
                    welcomeImage.setImageResource(R.drawable.fourtothree);
                    break;
                case 1:
                    welcomeImage.setImageResource(R.drawable.threetotwo);
                    break;
                case 2:
                    welcomeImage.setImageResource(R.drawable.sixteentonine);
                    break;
            }
        }else {
            welcomeImage.setImageResource(R.drawable.image_placeholder_donotcommit);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
