#!/usr/bin/python
# coding:utf-8
import unittest
from utils import *


class TestUtils(unittest.TestCase):
    def setUp(self):
        print 'setUp...'

    def tearDown(self):
        print 'tearDown...'

    def test_is_working_hour(self):
        print "now is working hour? ", is_working_hour(datetime.datetime.now())
        # "2016-04-16 11:30"
        self.assertTrue(is_working_hour(datetime.datetime(2016, 4, 8, 11, 30)))
        # "2016-04-08 11:29"
        self.assertTrue(is_working_hour(datetime.datetime(2016, 4, 8, 11, 29)))
        # "2016-04-08 09:14"
        self.assertFalse(is_working_hour(datetime.datetime(2016, 4, 8, 9, 14)))
        # "2016-04-08 09:15"
        self.assertTrue(is_working_hour(datetime.datetime(2016, 4, 8, 9, 15)))
        # "2016-04-08 13:00"
        self.assertTrue(is_working_hour(datetime.datetime(2016, 4, 8, 13, 0)))
        # "2016-04-08 12:59"
        self.assertFalse(is_working_hour(datetime.datetime(2016, 4, 8, 12, 59)))
        # "2016-04-08 15:00"
        self.assertTrue(is_working_hour(datetime.datetime(2016, 4, 8, 15, 0)))
        # "2016-04-08 15:01"
        self.assertFalse(is_working_hour(datetime.datetime(2016, 4, 8, 15, 1)))

    def test_is_working_day(self):
        self.assertFalse(is_working_day(datetime.datetime(2016, 4, 16)))
        self.assertTrue(is_working_day(datetime.datetime(2016, 4, 15)))

    def test_get_page(self):
        doc = parse_page("http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0")
        print doc


if __name__ == '__main__':
    unittest.main()
