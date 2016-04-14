#!/usr/bin/python
# coding:utf-8

import tushare as ts
import redis
import datetime
import os

db_name = "stock"
db_path = "/home/eryk/workspaces/cheetah/python/db/"
db_suffix = datetime.datetime.now().strftime("%Y-%m-%d")

try:
    import cPickle as pickle
except ImportError:
    import pickle


def get_conn():
    return redis.Redis(host='112.124.60.26', port=6399)


def save_db(stock_list):
    r = get_conn()
    r.set('stock_list', pickle.dumps(stock_list))


def load_db():
    r = get_conn()
    return pickle.loads(r.get('stock_list'))


def get_db_file_name():
    return db_path + db_name + "_" + db_suffix


def save_file(stock_list):
    f = open(get_db_file_name(), 'wb')
    pickle.dump(stock_list, f)
    f.close()


def load_file():
    db_full_path = get_db_file_name()
    if os.path.exists(db_full_path):
        if os.path.getsize(db_full_path) == 0:
            stock_list = ts.get_stock_basics()
            save_file(stock_list)
    else:
        stock_list = ts.get_stock_basics()
        save_file(stock_list)
    f = open(db_full_path, 'rb')
    objects = pickle.load(f)
    f.close
    return objects

if __name__ == "__main__":
    new_stock_list = load_file()
    print new_stock_list