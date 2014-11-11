/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;

/**
 * Created by fsuda on 25.08.2014.
 */
public class WelcomeScreenFragment extends Fragment {
    public static final int PAGE_COUNT = 3;
    public static final String ARG_WELCOME_PAGE_NUMBER = "welcome_page_number";


    //*******FRAGMENT LIFECYCLE************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        int imageId;
        int textId;
        int headerTextId;

        Bundle args = getArguments();
        int pageNumber = 0;

        if(args!=null) {
            pageNumber = args.getInt(ARG_WELCOME_PAGE_NUMBER);
        }

        switch (pageNumber) {
            case 0:
            default:
                imageId = R.drawable.create;
                textId  = R.string.welcome_page_create;
                headerTextId = R.string.welcome_page_create_header;
                break;
            case 1:
                imageId = R.drawable.add;
                textId  = R.string.welcome_page_add;
                headerTextId = R.string.welcome_page_add_header;
                break;
            case 2:
                imageId = R.drawable.match;
                textId  = R.string.welcome_page_match;
                headerTextId = R.string.welcome_page_match_header;
                break;
        }

        ImageView welcomeImage = (ImageView) rootView.findViewById(R.id.welcome_image);
        TextView welcomeHeaderText = (TextView) rootView.findViewById(R.id.welcome_header);
        TextView welcomeText = (TextView) rootView.findViewById(R.id.welcome_text);

        welcomeImage.setImageResource(imageId);
        welcomeText.setText(textId);
        welcomeHeaderText.setText(headerTextId);
        return rootView;
    }
    //*************************************************************************************************
}
