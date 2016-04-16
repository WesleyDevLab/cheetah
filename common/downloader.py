#!/usr/bin/python
#coding:utf-8

from selenium import webdriver


def download(url):
    driver = webdriver.PhantomJS()
    driver.get(url)
    html = driver.page_source
    driver.close()
    return html
