package com.ttp.lstchatapp.Holder;

import android.os.Bundle; //Bundle (hashmap data structure with key-vale, it's used when giving and getting data with putextra

/**
 * Created by 0047TiTANplateform_ on 2018-01-17.
 */

public class QBUnreadMessageHolder {
    private static QBUnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance() {
        QBUnreadMessageHolder qbUnreadMessageHolder;
        synchronized (QBUnreadMessageHolder.class) {
            if (instance == null) {
                instance = new QBUnreadMessageHolder();
            }
            qbUnreadMessageHolder = instance;

            return qbUnreadMessageHolder;
        }
    }

    private QBUnreadMessageHolder() {
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {return this.bundle;}

    public int getUnreadMessageByDialogId(String id) {return  this.bundle.getInt(id);}


}
