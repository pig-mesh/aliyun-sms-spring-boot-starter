# aliyun-sms

阿里云 SMS 短信 Java SDK 封装

Fork Github & Issues: [https://github.com/cn-src/aliyun-sms](https://github.com/cn-src/aliyun-sms)

# spring boot 集成

## 1. 添加依赖

``` xml
<dependency>
    <groupId>io.springboot.sms</groupId>
    <artifactId>aliyun-sms-spring-boot-starter</artifactId>
    <version>2.0.2</version>
</dependency>
```

## 2. 配置短信元信息

```
aliyun:
  sms:
    SMS_186401263:  #阿里云短信模板ID1
      accessKeyId:  #阿里云AK
      accessKeySecret: #阿里云SK
      signName: #阿里云签名名
    SMS_186401263:  #阿里云短信模板ID2
      accessKeyId:  #阿里云AK
      accessKeySecret: #阿里云SK
      signName: #阿里云签名名
```

## 3. 代码使用

3. 单一模板使用: 使用默认模板发送 code

```java
@Autowired
private SmsClient smsClient;

// 第一个参数: 验证码 code 参数
// 第个个参数: 手机号可变参数 1，2，3，4
this.smsClient.sendCode("1234","17034642119")
```

4. 多个模板使用

```java
    @Autowired
private SmsClient smsClient;

// 第一个参数: 模板ID 也是就是你想用哪个模板发
// 第二个参数: 验证码 code 参数
// 第三个参数: 手机号可变参数 1，2，3，4
this.smsClient.sendCodeByKey("SMS_186401263","1234","17034642119")
```

5. 多个参数使用

```java
    @Autowired
private SmsClient smsClient;

// 第一个参数: 模板ID 也是就是你想用哪个模板发
// 第二个参数: 参数Map
// 第三个参数: 手机号可变参数 1，2，3，4
Map<String,String > params = new HashMap<>();
params.put("code","123");
params.put("var2","456");
this.smsClient.sendParamByKey("SMS_186401263", params, "17034642119");
```

[官方文档:https://help.aliyun.com/document_detail/55284.html?spm=5176.8195934.1001856.3.5cd64183fNqodO](https://help.aliyun.com/document_detail/55284.html?spm=5176.8195934.1001856.3.5cd64183fNqodO)
