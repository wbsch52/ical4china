# ical4china

### Introduction
一个能生成当前年份的中国法定节假日的iCalendar文件的小程序。

写这个小程序的初衷是因为自己的手机和电脑都是苹果设备，用苹果设备的相信大家都懂自带的日历软件无法看出何时放假何时需要补班。

---
### Getting Started
数据通过[聚合数据](https://www.juhe.cn)平台获取，需要注册聚合数据平台账号并提供appKey

使用/安装:

环境: JDK 1.8+

- 直接下载程序包解压后运行

- 将项目克隆至本地自行编译打包
```
cd ${project_home}
mvn clean package
```

目前提供以下两种方式生成iCalendar文件:

- 本地方式： 直接将iCalendar文件到指定目录下，此方式便于不想搭建服务器的同学直接将今年全年的节假日导出，方便快捷但需要每年生成一次。
```
java -cp ical4china.jar com.simon.ical.cli.CommandLineTools -k ${your_key} -o ${out_path}
// NOTE: 执行 java -cp ical4china.jar com.simon.ical.cli.CommandLineTools -h 可查看使用帮助
```

- 订阅方式： 以Web方式运行，提供订阅接口直接在mac端进行日历订阅。订阅链接：webcal://localhost:8080/ical

提供application.properties:
```properties
ical4china.juhe.app-key=${your_key}
```

```
java -jar ical4china.jar --spring.config.location=${config_path}
```
---

