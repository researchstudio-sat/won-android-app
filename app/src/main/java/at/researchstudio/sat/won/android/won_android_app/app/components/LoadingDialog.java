package at.researchstudio.sat.won.android.won_android_app.app.components;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by fsuda on 25.09.2014.
 */
public class LoadingDialog extends ProgressDialog {
    AsyncTask asyncTask;

    public LoadingDialog(Context context, AsyncTask aysncTask) {
        //TODO: STYLE LOADING DIALOG
        super(context);
        this.asyncTask = aysncTask;
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.setCanceledOnTouchOutside(false);
        this.setIndeterminate(true); //Shows undefined amount of time e.g. spinner no progress values needed

        this.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("Progress setOnCancelListener", "called onCancel");
                if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                    asyncTask.cancel(true);
                }
            }
        });
    }
}
