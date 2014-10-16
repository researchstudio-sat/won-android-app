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

package at.researchstudio.sat.won.android.won_android_app.app.service;

/**
 * Created by fsuda on 10.10.2014.
 */
import java.io.File;
import android.content.Context;
import at.researchstudio.sat.won.android.won_android_app.app.util.FileUtils;

public class FileCache {
    private static final String LOG_TAG = FileCache.class.getSimpleName();

    private File cacheDir;

    public FileCache(Context context){
        //Find the dir to save cached images
        cacheDir = FileUtils.getAlbumStorageDir();
    }

    public File getFile(String url){
        if(url.startsWith("/")){
            return new File(url);
        }else{
            return new File(cacheDir, String.valueOf(url.hashCode()));
        }
    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}
