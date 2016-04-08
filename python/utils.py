#!/usr/bin/python
# coding:utf-8
import datetime


def f(num):
    """
    结果四舍五入保留两位小数

    :param num: 待格式化数字
    :return:
    """
    return round(float(num), 2)


def get_start_date(n):
    """
    获取n天前的日期
    :param n:
    :return:
    """
    day = datetime.date.today()
    return day + datetime.timedelta(-n)


def is_working_day(dt):
    """
    检查某天是否是工作日，周一为0
    :param dt:
    :return:
    """
    if 0 <= dt.weekday() <= 4:
        return True
    else:
        return False


def is_working_hour(dt):
    """
    检查今天是否是交易时间段
    :param dt:
    :return:
    """
    if dt.time().hour <= 9 and dt.time().minute < 15:
        return False
    elif (dt.time().hour >= 11 and dt.time().minute > 30) and (dt.time().hour < 13):
        return False
    elif dt.time().hour >= 15 and dt.time().minute > 0:
        return False
    else:
        return True


if __name__ == "__main__":
    print is_working_hour(datetime.datetime.now())
    print "2016-04-08 11:30", is_working_hour(datetime.datetime(2016, 4, 8, 11, 30))
    print "2016-04-08 11:29", is_working_hour(datetime.datetime(2016, 4, 8, 11, 29))
    print "2016-04-08 09:14", is_working_hour(datetime.datetime(2016, 4, 8, 9, 14))
    print "2016-04-08 09:15", is_working_hour(datetime.datetime(2016, 4, 8, 9, 15))
    print "2016-04-08 13:00", is_working_hour(datetime.datetime(2016, 4, 8, 13, 0))
    print "2016-04-08 12:59", is_working_hour(datetime.datetime(2016, 4, 8, 12, 59))
    print "2016-04-08 15:00", is_working_hour(datetime.datetime(2016, 4, 8, 15, 0))
    print "2016-04-08 15:01", is_working_hour(datetime.datetime(2016, 4, 8, 15, 1))