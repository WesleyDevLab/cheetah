package com.zhaijiong.datacenter;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class CheetahDB {

    private DB db = DBMaker.memoryDB().make();

    public void test(){
    }
}
