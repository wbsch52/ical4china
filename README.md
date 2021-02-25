# ical4china

### Introduction
一个能生成当前年份的中国法定节假日的iCalendar文件的小程序。

写这个小程序的初衷是因为自己的手机和电脑都是苹果设备，用苹果设备的相信大家都懂自带的日历软件无法看出何时放假何时需要补班。
自己又有点小洁癖不愿意使用第三方的日历软件，在网上也没有找到靠谱的iCalendar订阅服务所以自己造了个轮子，也希望这个程序能够帮助更多与我有相同痛点的人。

---
### Getting Started
数据通过[聚合数据](https://www.juhe.cn)平台获取，需要注册聚合数据平台账号并提供appKey

修改application.properties下的:

```
ical4china.juhe.app-key=${your key}
```