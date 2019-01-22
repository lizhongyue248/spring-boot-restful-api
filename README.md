![u24205958681698952353fm26gp0jpg](https://resources.echocow.cn//file/2019/01/7345016a48f34bc3b34279be1c988b6c_u24205958681698952353fm26gp0.jpg) 

前段时间写了一篇博客，[spring boot restful API 从零到一完整实践 ](https://echocow.cn/articles/2019/01/05/1546684795983.html) ，通过上篇文章构建了两个版本的 restful api，这篇博客，则是要在这基础上面，添加一个安全措施，我选择的是 oauth2 和 jwt 进行保护我们的 API，通过 spring security oauth2 进行一步一步的配置我们的安全 API 接口服务。


> 博客地址：[https://echocow.cn](https://echocow.cn)
>
> 项目地址：[github-security-one 分支](https://github.com/lizhongyue248/spring-boot-restful-api/tree/security-one)

# Oauth2

OAuth 2.0关注客户端开发者的简易性。要么通过组织在资源拥有者和HTTP服务商之间的被批准的交互动作代表用户，要么允许第三方应用代表用户获得访问的权限。同时为Web应用，桌面应用和手机，和起居室设备提供专门的认证流程。[百度百科](https://baike.baidu.com/item/OAuth2.0/6788617?fr=aladdin)。这篇文章同上一篇一样，将会详细记录如何使用 spring security oauth2 进行构建一个安全 restful api ，oauth2 的概念和核心我不再赘述，具体可查看文末的参考链接。在这之前，我们需要先做一番准备。

# 这篇文章能够带给你什么
使用 spring security oauth2 autoconfigure 自动配置一个简单的 oauth2 认证

# 在这之前 

你需要拥有一个已经能够成功构建起来的 spring boot 的项目，它能够正常启动与访问访问，在这里我们使用上一篇 [spring boot restful API 从零到一完整实践 ](https://echocow.cn/articles/2019/01/05/1546684795983.html) 构建的 restful API 作为基础。如果你需要快速体验，你只需要建立一个拥有 Helloworld api 接口的新项目即可。然后你需要加入以下依赖（gradle）
```
// 提供 spring security 支持
implementation('org.springframework.boot:spring-boot-starter-security')
// 提供 oauth2 自动化配置
implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:${springBootVersion}")
```
![在这之前](https://resources.echocow.cn//file/2019/01/49c932992ee34adebd7b3c3e624d92a2_image.png) 

**同时你必须具备如下知识：**

1. oauth2 中什么是 授权服务器
2. oauth2 中什么是 资源服务器
3. oauth2 中的四种授权模式（我们会使用到 授权码 模式和 密码 模式）
4. spring security 的部分知识
5. jpa 的使用

以上概念本文不再提起，如有疑惑可选择去文末的参考链接自行选择性学习。

## 请求 url
虽然上面说明了需要知道的东西，但是我在这里还是需要对我们需要使用到的 url 进行一个简单说明，但是参数我不再详说。

- /oauth/authorize    GET  授权码模式获取授权码
- /oauth/token        POST 获取token、刷新token
- /oauth/check_token  POST 检测 token


# 自动配置
spring boot 之所以能够如此受欢迎，最大的原因莫过于他提供的模板配置以及自动配置，我们甚至不需要写什么代码，只需要见得修改一下配置文件即可构建一个基于内存的简单的安全服务，所以最重要的，需要先配置一个 **授权服务器**，通过它下发令牌

## 使用默认配置

1、 添加注解：你需要为我们启动类添加一个启动的注解

![授权服务器](https://resources.echocow.cn//file/2019/01/eec9a5a1d76b4645b6d4517d61a0233f_image.png) 

2、 我们通过一个注解就已经完成一个安全的授权的创建，运行查看输出日志。

![使用默认配置](https://resources.echocow.cn//file/2019/01/ef02659d7bc74ce48528c9a6484195d2_image.png) 

3、 携带生成的 client id 进行访问，这里 /api 是我自己添加项目路径，如果没有添加亲直接访问 /oauth/authorize

```
http://localhost:8080/api/oauth/authorize?response_type=code&client_id=d7003bdc-981c-4745-9eb4-673028b4c4e0&redirect_uri=http://example.com&scope=all
```

![imagepng](https://resources.echocow.cn//file/2019/01/5821ad7971024e67a7aa0c478f9d6980_image.png) 

4、 访问报错，这是因为我们没有配置 spring security 造成的，所以需要回去配置一下，使用默认配置即可

![spring security ](https://resources.echocow.cn//file/2019/01/46bac0e60a024af89ec55896509e6743_image.png) 

5、 再次运行，跳转登录界面

![访问](https://resources.echocow.cn//file/2019/01/1d480c70ade941749ad2df5ee2e15358_image.png) 

6、 用户名 `user`，密码为刚才生成的 随机密码,登录

![登录](https://resources.echocow.cn//file/2019/01/9d9aede6f73b4521995753bc74583dbe_image.png) 

7、 修改配置文件。注册一下回调地址

![修改配置文件](https://resources.echocow.cn//file/2019/01/6a206b4ad888453cb3d53d0cf5d33495_image.png) 

8、 重启，使用新生成的 client id ，重新访问以及登录

![登录成功](https://resources.echocow.cn//file/2019/01/15d8ea79040349fc8b69ee18d5aa91d8_image.png) 

9、 选择 Approve 同意授权，获取到授权码

![Approve](https://resources.echocow.cn//file/2019/01/7e68ec23752b47c18e4b20468be9bffe_image.png) 

10、 使用授权码请求token 

![使用授权码请求token](https://resources.echocow.cn//file/2019/01/9b9bbf0762e042a0aec1ac1c3f9d18ec_image.png) 

![使用授权码请求token](https://resources.echocow.cn//file/2019/01/45f06da95b294d75a4a1105bb783c3ac_image.png) 

![获取成功](https://resources.echocow.cn//file/2019/01/eaa2b00de92643f289e61d850c341312_image.png) 

这就是使用他的自动配置的认证过程，接下来我们通过配置文件进行控制他的自动配置

## 自定义配置

1、 修改 spring boot 的一些默认配置

![imagepng](https://resources.echocow.cn//file/2019/01/b4e0be84b5864f5a8837535f35365d13_image.png) 

2、 配置两个用户

![imagepng](https://resources.echocow.cn//file/2019/01/32d02da8d3124b43be9a308a2b1e539e_image.png) 

3、 现在我们拥有两个用户了，就可以去使用密码模式获取 token 了。

![imagepng](https://resources.echocow.cn//file/2019/01/ca3031eec3484516be64a136d7e853b2_image.png) 

![imagepng](https://resources.echocow.cn//file/2019/01/07cfd4841e884e0fa62c8a4b98be990e_image.png) 

![imagepng](https://resources.echocow.cn//file/2019/01/356671ef064c41249b3bdc36b9c58780_image.png) 

## 测试访问
1、 直接访问我们已有的资源

![直接访问我们已有的资源](https://resources.echocow.cn//file/2019/01/ce2affc8848b4ad0ba4944f77b501970_image.png) 

2、 携带上一步获取的 token 访问

![携带上一步获取的 token 访问](https://resources.echocow.cn//file/2019/01/32fae0a7f55044618a338d33b40a56ac_image.png) 

发现还是失败，为什么呢？因为我么并没有开启 资源服务器 他没办法进行验证，所以我们接下来就是开启一个资源服务器

## 开启 资源服务器
同样，一个注解即可

![资源服务器](https://resources.echocow.cn//file/2019/01/ed740b7613af4bf5bfe73bd4c8ec5e24_image.png) 

重新获取token后，携带 token 访问

![携带 token 访问](https://resources.echocow.cn//file/2019/01/73f58d0b76564206bf42712234f606ef_image.png) 

## 解析 token

1、 尝试解析 token

![尝试解析 token](https://resources.echocow.cn//file/2019/01/05163891823d4a768e0ba1c3e6a942e4_image.png) 

2、 403，我们需要配置以支持 token 解析。

![支持 token 解析](https://resources.echocow.cn//file/2019/01/96a634bddeea42f495fbf13120c3f4a1_image.png) 

3、 重启后获取token再次解析

![重启后获取token再次解析](https://resources.echocow.cn//file/2019/01/9dc42141faa1418a87f902c141b16398_image.png) 

## 刷新 token 

> 遗憾的是，如果只是用配置文件，是不能够 刷新 token 的，至少我没有成功。

![imagepng](https://resources.echocow.cn//file/2019/01/79d18b46df3c41c2b65d31e2fad9d97f_image.png) 


![imagepng](https://resources.echocow.cn//file/2019/01/b8eff0fb4910412fa1f0618f6c4883cb_image.png) 

发现不行，查看原因

![imagepng](https://resources.echocow.cn//file/2019/01/44b4875d231e44a4b4466bced77d801c_image.png) 

可惜通过尝试各种办法都不行（在不增加类的情况下）解决办法参见

- [stackoverflow：spring-security-oauth2 2.0.7 refresh token UserDetailsService Configuration - UserDetailsService is required](https://stackoverflow.com/questions/30454480/spring-security-oauth2-2-0-7-refresh-token-userdetailsservice-configuration-us)
- [segmentfault: spring security oauth2之refresh token](https://segmentfault.com/a/1190000012338044)


使用他的配置文件，我们不需要写任何代码，就完成了一个简单的内存认证，甚至我们可以直接通过修改 userDetailsService 完成用户的认证，不过也发现了，他使用配置文件的方式功能很有限，局限性很强，不能够刷新 token 是一个痛点啊，所以我们更期望于手动配置。



# 参考链接

1. [理解OAuth 2.0  阮一峰](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)
2. [Oauth2 授权](https://www.jianshu.com/p/9d0264d27c3b)
3. [spring oauth2 auto config](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
