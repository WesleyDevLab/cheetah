#!/usr/bin/python
# coding:utf-8

import time
from wxBot.wxbot import WXBot


class MyWXBot(WXBot):
    def handle_msg_all(self, msg):
        if msg['msg_type_id'] == 4 and msg['content']['type'] == 0:
            self.send_msg_by_uid('eryk86', msg['user']['id'])

    def schedule(self):
        self.send_msg('a马骏', '你好')
        time.sleep(3)

def main():
    bot = MyWXBot()
    # bot.conf['qr'] = 'tty'
    bot.DEBUG = True
    bot.run()

if __name__ == '__main__':
    main()