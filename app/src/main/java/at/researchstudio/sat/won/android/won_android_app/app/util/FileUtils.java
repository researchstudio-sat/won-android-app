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

package at.researchstudio.sat.won.android.won_android_app.app.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final String LOG_TAG = FileUtils.class.getSimpleName();
    private static final String TEMPALBUM_DIR = "won";
    private static final String TEMPIMAGE_EXTENSION = ".jpg";
    private static final String TEMPIMAGE_PREFIX = "img_";

    public static File getAlbumStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMPALBUM_DIR);

        file.mkdirs(); //CREATES DIR IF IT DOESNT EXIST YET

        return file;
    }

    public static File createNewImageFile() throws IOException{
        int i = 1;

        File f;
        do{
            f =  new File(FileUtils.getAlbumStorageDir(), TEMPIMAGE_PREFIX+i+TEMPIMAGE_EXTENSION);
            i++;
        }while(f.exists());

        Log.e(LOG_TAG, "New FilePath: "+f.getAbsolutePath());

        return f;
    }
}
