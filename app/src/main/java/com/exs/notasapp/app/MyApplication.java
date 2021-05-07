package com.exs.notasapp.app;

import android.app.Application;

import com.exs.notasapp.models.Board;
import com.exs.notasapp.models.Note;

import java.util.concurrent.atomic.AtomicInteger;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    public static AtomicInteger BoardID = new AtomicInteger();
    public static AtomicInteger NoteID = new AtomicInteger();

    @Override
    public void onCreate() {

        super.onCreate();
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        BoardID = getIdTable(realm, Board.class);
        NoteID = getIdTable(realm, Note.class);
        realm.close();

    }

    private void setUpRealmConfig(){

        Realm.init(getApplicationContext());

        RealmConfiguration config =  new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdTable(Realm realm,Class<T> anyClass){

        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();



    }

}
