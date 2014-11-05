package at.researchstudio.sat.won.android.won_android_app.app.model;

import java.util.UUID;

/**
 * Created by fsuda on 14.10.2014.
 */
public abstract class Model {
    protected static final String UUID_REF="UUID";
    private UUID uuid;


    protected Model(UUID uuid) {
        if(uuid==null){
            this.uuid = UUID.randomUUID();
        }else{
            this.uuid = uuid;
        }
    }

    public UUID getUuid(){
        return uuid;
    }

    public String getUuidString(){
        return uuid.toString();
    }

    public void setUuid(UUID uuid){
        this.uuid = uuid;
    }

    public void setUuid(String uuid){
        this.uuid = UUID.fromString(uuid);
    }
}
