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

    now = datetime.now()
    print now.strftime('%Y-%m-%d')

    strNow = '2012-01-03'
    nowDate =  time.strptime(strNow, "%Y-%m-%d")

    %a 星期几的简写 Weekday name, abbr.
    %A 星期几的全称 Weekday name, full
    %b 月分的简写 Month name, abbr.
    %B 月份的全称 Month name, full
    %c 标准的日期的时间串 Complete date and time representation
    %d 十进制表示的每月的第几天 Day of the month
    %H 24小时制的小时 Hour (24-hour clock)
    %I 12小时制的小时 Hour (12-hour clock)
    %j 十进制表示的每年的第几天 Day of the year
    %m 十进制表示的月份 Month number
    %M 十时制表示的分钟数 Minute number
    %S 十进制的秒数 Second number
    %U 第年的第几周，把星期日做为第一天（值从0到53）Week number (Sunday first weekday)
    %w 十进制表示的星期几（值从0到6，星期天为0）weekday number
    %W 每年的第几周，把星期一做为第一天（值从0到53） Week number (Monday first weekday)
    %x 标准的日期串 Complete date representation (e.g. 13/01/08)
    %X 标准的时间串 Complete time representation (e.g. 17:02:10)
    %y 不带世纪的十进制年份（值从0到99）Year number within century
    %Y 带世纪部分的十制年份 Year number
    %z，%Z 时区名称，如果不能得到时区名称则返回空字符。Name of time zone
    %% 百分号

    :rtype : object
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