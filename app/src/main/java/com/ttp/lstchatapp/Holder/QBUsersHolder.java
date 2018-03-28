package com.ttp.lstchatapp.Holder;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 0047TiTANplateform_ on 2018-01-16.
 */

public class QBUsersHolder {

    private static QBUsersHolder instance;
    private SparseArray<QBUser> qbUsersHolderSparseArray;

    public static synchronized QBUsersHolder getInstance() {
        if (instance == null) {
            {
                instance = new QBUsersHolder();
            }
        }
        return instance;
    }

    private QBUsersHolder() {
        qbUsersHolderSparseArray = new SparseArray<QBUser>();
    }

    public void putUsers(List<QBUser> users) {
        for(QBUser user:users) putUser(user);
    }

   public void putUser(QBUser user) {
        qbUsersHolderSparseArray.put(user.getId(), user);
    }

    public QBUser getUserById(int id ){
        return qbUsersHolderSparseArray.get(id);
    }

    public List<QBUser> getUsersByIds(List<Integer> ids){
        List<QBUser> qbUser = new ArrayList<>();
        for(Integer id:ids){
            QBUser user = getUserById(id);
            if(user != null){
                qbUser.add(user);
            }
        }
        return qbUser;
    }


    public ArrayList<QBUser> getAllUsers() {

        ArrayList<QBUser> result = new ArrayList<>();
        for (int i = 0; i<qbUsersHolderSparseArray.size(); i++) result.add(qbUsersHolderSparseArray.valueAt(i));
        return result;

    }
}
