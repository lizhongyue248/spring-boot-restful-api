# spring boot restful API 从零到一完整实践

![](https://img.hacpai.com/bing/20180819.jpg?imageView2/1/w/960/h/520/interlace/1/q/100)

自己第一次接触 restful 是在学习 vue 的时候，第一次看见的时候，真的打心底里的喜欢。不仅是因为其一致的规范性，还有他的简单明了，都让我眼前一亮的感觉。现在对于一些程序，都是提倡的前后端分离，各干各的互不相干，事实上我也非常喜欢这种方式，虽然我希望的是成为一个全栈的工程师。但是前后端的分离却带来了一些质的飞跃，一方面业务上的逻辑不会太耦合，另一方面让更专业的人处理更专业的事，效率和质量上都会高上许多。Restful Api 是目前比较成熟的一套互联网应用程序的 API 设计理论，就是作为其中一种统一的机制出现，方便不同的前端设备与后端进行通信。今天就利用 spring boot 的多个组件，来实现以下 restful 风格的 api，从自己使用 controller 到使用框架开始一步一步搭建。

# RESTFul
RESTFUl 一种软件架构风格、设计风格，而不是标准，只是提供了一组设计原则和约束条件（源自[百度百科](https://baike.baidu.com/item/RESTful/4406165?fr=aladdin)）。这是一篇如何使用 spring boot 来进行构建一个 restful Api 的记录，他的概念和核心我不再赘述，具体可查看文末的参考链接。在这之前，我们需要对我们 Api 进行一个简单的设计。

# 这篇文章能够带给你什么

1. 如何设计一个 Restful 风格的 Api
2. 项目开发的数据初始化
3. 通过 spring boot 实现 Api，v1 版本
4. 通过 spring boot 进行统一异常处理
5. 通过 spring data rest 实现 Api，v2 版本
6. 通过 spring data rest 进行参数校验
7. 自定义 spring data rest 查询、删除等方法
8. 接口测试工具以及测试spring mvc

# 需要做好的规划

## Api 基本设计
1. 我们 api 足够简单，我们为他准备一个基础路径，即

```
http://localhost:8080/api/
```

2. 我们 api 有多个版本，这里暂且定为一个v1版本，即

```
http://localhost:8080/api/v1
```

另一种做法是，将版本号放在HTTP头信息中，但不如放入URL方便和直观。[Github](https://developer.github.com/v3/media/#request-specific-version)采用这种做法。

3. 我们选用五个常用的HTTP动词

-   GET（SELECT）：从服务器取出资源（一项或多项）。
-   POST（CREATE）：在服务器新建一个资源。
-   PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
-   PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
-   DELETE（DELETE）：从服务器删除资源。

4. 服务器向用户返回的状态码和提示信息，我们用到的有以下一些

-   200 OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）。
-   201 CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功。
-   204 NO CONTENT - [DELETE]：用户删除数据成功。
-   400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
-   404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
-   500 INTERNAL SERVER ERROR - [*]：服务器发生错误，用户将无法判断发出的请求是否成功。

## Api 数据准备

我们需要为数据准备一个实体，我将使用一个书单（Book）对象作为实体，他的具体属性如下：

- id
- name  书名
- author  作者
- description  描述
- status  状态

## Api Url 设计

按照我们提供的五个动词，分别设计多个 api 如下：

* GET     /api/v1/books        所有书单
* GET     /api/v1/books/{id}   获取一条书单
* POST    /api/v1/books        新建一条书单
* PUT     /api/v1/books/{id}   更新一条书单，提供全部信息
* PATCH   /api/v1/books/{id}   更新一条书单，提供部分信息
* DELETE  /api/v1/books/{id}   删除一条书单
* DELETE  /API/v1/books        删除所有书单，危险操作

## 技术选型

- 核心框架：spring boot
- web： spring boot web
- 数据库：mysql
- 构建工具：gradle
- 应用框架：spring boot data jpa
- restful：spring data rest
- 工具支持：spring boot devtools
- 测试框架：junit5、spring boot test
- 开发工具：idea

# 环境搭建

首先我们要先通过 idea 对项目进行初始化

## 初始化
1. 新建项目
![新建项目](https://resources.echocow.cn//file/2019/01/1cf250f196d3424da1c2d7c4011a2659__sunawtX11XDialogPeer_20190105191714.png)

2. 填写基本属性
![填写基本属性](https://resources.echocow.cn//file/2019/01/ee1555cf87bc4cde9ac182408eab08c6__sunawtX11XDialogPeer_20190105191934.png)

3. 选择依赖
![选择依赖](https://resources.echocow.cn//file/2019/01/99b95787f4d9486691946ad183c866ea__sunawtX11XDialogPeer_20190105192653.png)

4. 设置 gradle
![设置 gradle](https://resources.echocow.cn//file/2019/01/af6c9b055e9043acbb5b6641be6d35c2__sunawtX11XDialogPeer_20190105192740.png)

5. 等待构建依赖的同时，修改一下仓库地址，不然下载很慢，如果一直下不下载就修改好仓库地址后重新打开idea让他自动重下。这就是为啥不喜欢直接建spring 的 gradle 项目的原因，他会自动导入，个人喜欢直接建 gradle 项目然后手动导入依赖。但是对于 spring 来说他也确实方便。
![修改仓库地址](https://resources.echocow.cn//file/2019/01/64a8fe853f3b4535a43880edbfca7f0c__sunawtX11XFramePeer_20190105212711.png)

6. 配置 spring boot 项目
![配置](https://resources.echocow.cn//file/2019/01/c0e2a8e7136e480abd24412a1b46f020__sunawtX11XFramePeer_20190105213053.png)

```
spring:
  application:
    name: restful-api
  datasource:
    url: jdbc:mysql://localhost:3306/spring
    username: root
    password: 123456
    platform: mysql
  jpa:
    show-sql: true
 hibernate:
      ddl-auto: create
server:
  servlet:
    context-path: /api
```
这样我们就完成一个项目的初始化，接下来进行数据的准备

## 数据准备
1. 按照我们前面给出 Book 对象，建立实体类。
![Book](https://resources.echocow.cn//file/2019/01/fa4e69192fc149d598fb3e668328c4f2__sunawtX11XFramePeer_20190105213745.png)
```java
package cn.echocow.restfulapi.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 书籍的实体类
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:36
 */
@Entity
@Data
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(insertable = false, length = 20, nullable = false)
  public Long id;
  @NotNull
  @Column(columnDefinition = "varchar(50) comment '书名'")
  public String name;
  @NotNull
  @Column(columnDefinition = "varchar(25) comment '作者'")
  public String author;
  @Column(columnDefinition = "varchar(255) comment '描述'")
  public String description;
  @NotNull
  @ColumnDefault("1")
  @Column(columnDefinition = "tinyint(1) comment '是否存在'")
  public Boolean status;
}
```

2. 建立生成数据的 sql 文件
![_sunawtX11XFramePeer_20190105214010png](https://resources.echocow.cn//file/2019/01/ad4fc674b062494f80f09caf0d644eb9__sunawtX11XFramePeer_20190105214010.png)


```sql
INSERT INTO spring.book (id, author, description, name, status) VALUES (1, '孟宁', '本书从理解计算机硬件的核心工作机制（存储程序计算机和函数调用堆栈）和用户态程序如何通过系统调用陷入内核（中断异常）入手，通过上下两个方向双向夹击的策略，并利用实际可运行程序的反汇编代码从实践的角度理解操作系统内核，分析Linux内核源代码，从系统调用陷入内核、进程调度与进程切换开始，最后返回到用户态进程。', '庖丁解牛Linux内核分析', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (2, '孙亮', '大数据时代为机器学习的应用提供了广阔的空间，各行各业涉及数据分析的工作都需要使用机器学习算法。本书围绕实际数据分析的流程展开，着重介绍数据探索、数据预处理和常用的机器学习算法模型。本书从解决实际问题的角度出发，介绍回归算法、分类算法、推荐算法、排序算法和集成学习算法。在介绍每种机器学习算法模型时，书中不但阐述基本原理，而且讨论模型的评价与选择。为方便读者学习各种算法，本书介绍了R语言中相应的软件包并给出了示例程序。', '实用机器学习', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (3, '托马斯·哈斯尔万特', '本书以基础的统计学知识和假设检验为重点，简明扼要地讲述了Python在数据分析、可视化和统计建模中的应用。主要包括Python的简单介绍、研究设计、数据管理、概率分布、不同数据类型的假设检验、广义线性模型、生存分析和贝叶斯统计学等从入门到高级的内容。', 'Python统计分析', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (4, '甘迪文', '《Windows黑客编程技术详解》介绍的是黑客编程的基础技术，涉及用户层下的Windows编程和内核层下的Rootkit编程。本书分为用户篇和内核篇两部分，用户篇包括11章，配套49个示例程序源码；内核篇包括7章，配套28个示例程序源码。本书介绍的每个技术都有详细的实现原理，以及对应的示例代码（配套代码均支持32位和64位Windows 7、Windows 8.1及Windows 10系统），旨在帮助初学者建立起黑客编程技术的基础。', 'Windows黑客编程技术详解', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (5, '科里•奥尔索夫', '本书作者是一名自学成才的程序员，经过一年的自学，掌握了编程技能并在eBay找到了一份软件工程师的工作。本书是作者结合个人经验写作而成，旨在帮助读者从外行成长为一名专业的Python程序员。', 'Python编程无师自通——专业程序员的养成', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (6, '威廉·史密斯', '本书由浅入深地详细讲解了计算机存储使用的多种数据结构。本书首先讲解了初级的数据结构（如表、栈、队列和堆等），具体包括它们的工作原理、功能实现以及典型的应用程序等；然后讨论了数据结构，如泛型集合、排序、搜索和递归等；最后介绍了如何在日常应用中使用这些数据结构。', '程序员学数据结构', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (7, '张鑫旭', '本书从前端开发人员的需求出发，以“流”为线索，从结构、内容到美化装饰等方面，全面且深入地讲解前端开发人员必须了解和掌握的大量的CSS知识点。同时，作者结合多年的从业经验，通过大量的实战案例，详尽解析CSS的相关知识与常见问题。作者还为本书开发了专门的配套网站，进行实例展示、问题答疑。', 'CSS世界', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (8, '理查德·格里姆斯', '作为一门广为人知的编程语言，C++已经诞生30多年了，这期间也出现并流行过许多种编程语言，但是C++是经得起考验的。如此经典的编程语言，值得每一位编程领域的新人认真学习，也适合有经验的程序员细细品味。', 'C++编程自学宝典', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (9, '萨沙·戈德斯汀', '本书详细解释了影响应用程序性能的Windows、CLR和物理硬件的内部结构，并为读者提供了衡量代码如何独立于外部因素执行操作的知识和工具。书中提供了大量的C#代码示例和技巧，将帮助读者zui大限度地提高算法和应用程序的性能，提高个人竞争优势，使用更低的成本获取更多的用户。', '.NET性能优化', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (10, '李伟', '《C++模板元编程实战：一个深度学习框架的初步实现》以一个深度学习框架的初步实现为例，讨论如何在一个相对较大的项目中深入应用元编程，为系统性能优化提供更多的可能。', 'C++模板元编程实战：一个深度学习框架的初步实现', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (11, 'Ben Klemens 克莱蒙', '本书展现了传统C语言教科书所不具有相关技术。全书分', 'C程序设计新思维（第2版）', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (12, '王云', '本书遵循由浅入深、循序渐进的原则，讲解单片机开发经典案例。本书以YL51单片机开发板为平台，通过案例逐个讲解开发板上各个器件模块的使用及其编程方法，包括单片机最小系统、数码管显示原理、中断与定时器、数模\\模数转换工作原理、LCD液晶显示、串行口通信、步进电机驱动原理、PWM脉宽调制与直流电机等内容。', '51单片机C语言程序设计教程', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (13, '胡振波', '本书是一本介绍通用CPU设计的入门书，以通俗的语言系统介绍了CPU和RISC-V架构，力求为读者揭开CPU设计的神秘面纱，打开计算机体系结构的大门。', '手把手教你设计CPU——RISC-V处理器篇', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (14, '克劳斯·福勒', '本书旨在通过实际的Python 3.0代码示例展示Python与数学应用程序的紧密联系，介绍将Python中的各种概念用于科学计算的方法。', 'Python 3.0科学计算指南', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (15, '路彦雄', '《文本上的算法 深入浅出自然语言处理》结合-作者多年学习和从事自然语言处理相关工作的经验，力图用生动形象的方式深入浅出地介绍自然语言处理的理论、方法和技术。本书抛弃掉繁琐的证明，提取出算法的核心，帮助读者尽快地掌握自然语言处理所必需的知识和技能。', '文本上的算法——深入浅出自然语言处理', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (16, '胡世杰', '本书从云存储的需求出发讲述对象存储的原理，循序渐进地建立起一个分布式对象存储的架构，并且将软件实现出来。全书共8章，分别涉及对象存储简介、可扩展分布式系统、元数据服务、数据校验和去重、数据冗余处理、断点续传、数据压缩和数据维护等。本书选择用来实现分布式对象存储软件的编程语言是当前流行的Go语言。', '分布式对象存储——原理、架构及Go语言实现', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (17, '徐子珊', '《趣题学算法》适于作为程序员的参考书，高校各专业学生学习“数据结构”“算法设计分析”“程序设计”等课程的扩展读物，也可以作为上述课程的实验或课程设计的材料，还可以作为准备参加国内或国际程序设计赛事的读者的赛前训练材料。', '趣题学算法', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (18, '鲁什迪·夏姆斯', '现如今，数据科学已经成为一个热门的技术领域，它涵盖了人工智能的各个方面，例如数据处理、信息检索、机器学习、自然语言处理、数据可视化等。而Java作为一门经典的编程语言，在数据科学领域也有着杰出的表现。', 'Java数据科学指南', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (19, '罗炳森', '结构化查询语言（Structured Query Language，SQL）是一种功能强大的数据库语言。它基于关系代数运算，功能丰富、语言简洁、使用方便灵活，已成为关系数据库的标准语言。', 'SQL优化核心思想', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (20, '弗兰克·D.卢娜', 'Direct3D是微软公司DirectX SDK集成开发包中的重要组成部分，是编写高性能3D图形应用程序的渲染库，适用于多媒体、娱乐、即时3D动画等广泛和实用的3D图形计算领域。', 'DirectX 12 3D 游戏开发实战', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (21, '巴阿尔丁•阿扎米', 'Kibana是广泛地应用在数据检索和数据可视化领域的ELK中的一员。本书专门介绍Kibana，通过不同的用例场景，带领读者全面体验Kibana的可视化功能。', 'Kibana数据可视化', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (22, '郝佳', '《Spring源码深度解析（第2版）》从核心实现、企业应用和Spring Boot这3个方面，由浅入深、由易到难地对Spring源码展开了系统的讲解，包括Spring 整体架构和环境搭建、容器的基本实现、默认标签的解析、自定义标签的解析、bean的加载、容器的功能扩展、AOP、数据库连接JDBC、整合MyBatis、事务、SpringMVC、远程服务、Spring消息、Spring Boot体系原理等内容。', 'Spring源码深度解析（第2版）', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (23, 'Jon Bentley', '书的内容围绕程序设计人员面对的一系列实际问题展开。作者JonBentley以其独有的洞察力和创造力，引导读者理解这些问题并学会解决方法，而这些正是程序员实际编程生涯中到关重要的。', '编程珠玑（第2版•修订版）', 1);
INSERT INTO spring.book (id, author, description, name, status) VALUES (24, 'Mickey W. Mantle', '这是一本系统阐述面对混乱而容易失控的技术开发团队时，如何管理、建设和强化团队，成功交付开发成果的大作。两位作者Mickey W. Mantle和Ron Lichty以合起来近70年的开发管理经验为基础，通过深刻的观察和分析，找到了软件开发管理的核心问题——人的管理，并围绕如何真正理解程序员、找到合适的程序员、与程序员沟通这几个核心话题，一步步展开，扩展到如何以人为本地进行团队建设、管理和项目管理。', '告别失控：软件开发团队管理必读', 1);
```
3. 利用 idea 的数据库管理工具直接管理数据库
![数据库](https://resources.echocow.cn//file/2019/01/6d4916e39b8d4602bca9959cc21fb315__sunawtX11XFramePeer_20190105214128.png)


4. 启动应用进行测试，查看一下是否创建对应的表和数据
![启动测试](https://resources.echocow.cn//file/2019/01/482a7e8a9ea7464a9dab6741ece8c37b__sunawtX11XFramePeer_20190105214506.png)

这样就完成我们需要的环境，下面进行一些必要的测试工具安装。

## 测试工具
我们需要一些接口测试工具来进行辅助开发，以便更快的得到及时反馈,以下工具选择根据需要即可。
1. [postman  一款功能全面且强大的接口测试工具](https://www.getpostman.com/apps)
2. [idea plugin RestfulToolkit 一套 RESTful 服务开发辅助工具集。](https://plugins.jetbrains.com/plugin/10292-restfultoolkit)
3. 使用 spring-boot-starter-test 进行 mockMvc 测试
4. 其它...

# 初步实现

在这一步，我们会通过 rest controller 的方式进行创建一个 Restful 风格的 api。所以在这之前，我们要暂时不引入 spring boot 提供的 rest ，即 build.gradle 中的 `spring-boot-starter-data-rest'` 依赖,为什么？后面就知道啦。

![后面就知道啦](https://resources.echocow.cn//file/2019/01/2f4b96bdb97040fa87593b88da1605fe___20190105215444.png)

## 第一步：实现
1. 建立 BookRepository，对数据库进访问
![对数据库进访问](https://resources.echocow.cn//file/2019/01/451b676cde3a4bf7ac2f39b9e80cb7ca__sunawtX11XFramePeer_20190105215825.png)

2. 建立 BookController

> 为什么不要 service？在开发过程中，我们都是 controller、service、repository 三层的，在这里我将它省去了 service。一方面因为我没有太多的复杂的逻辑要处理，加了service反而让我多写几个类甚至几个接口，另一方面，在实际开发的过程中也完全没有必要按照这么个设计来，自己开发得爽，代码易读性高，质量棒就行了，没必要拿着一套死不放。小型应用中，没有复杂的逻辑，我基本不会去写 service 层的。

![建立controller](https://resources.echocow.cn//file/2019/01/681265dce3714d94ba99296fce66eb37__sunawtX11XFramePeer_20190105220057.png)

3. 书写具体逻辑
![1](https://resources.echocow.cn//file/2019/01/14abc1e30c774e829cddda7f2a2efb81__sunawtX11XFramePeer_20190105220547.png)

![2](https://resources.echocow.cn//file/2019/01/da149cd90e4a43ebbabfc3f52f163297__sunawtX11XFramePeer_20190105220733.png)

![3](https://resources.echocow.cn//file/2019/01/78e7945cbcbf4b6eb21d0f200723161b__sunawtX11XFramePeer_20190105220741.png)

```java
package cn.echocow.restfulapi.controller;

import cn.echocow.restfulapi.entity.Book;
import cn.echocow.restfulapi.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * rest 风格 api
 *
 * GET     /api/v1/books        所有书单
 * GET     /api/v1/books/{id}   获取一条书单
 * POST    /api/v1/books        新建一条书单
 * PUT     /api/v1/books/{id}   更新一条书单，提供全部信息
 * PATCH   /api/v1/books/{id}   更新一条书单，提供部分信息
 * DELETE  /api/v1/books/{id}   删除一条书单
 * DELETE  /API/v1/books        删除所有书单
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:59
 */
@RestController
@RequestMapping("/v1")
public class BookController {
  private final BookRepository bookRepository;
  @Autowired
  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  /**
   * 获取所有书单
   * GET     /api/v1/books        所有书单
   *
   * @return http 响应
   */
  @GetMapping("/books")
  public HttpEntity<?> books() {
	return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
  }

  /**
   * 获取一个书单 * GET     /api/v1/books/{id}   获取一条书单 * * @param id id
   * @return http 响应
   */  @GetMapping("/books/{id}")
  public HttpEntity<?> booksOne(@PathVariable Long id) {
	return new ResponseEntity<>(bookRepository.findById(id).get(), HttpStatus.OK);
  }

  /**
   * 添加一个书单
   * POST    /api/v1/books        新建一条书单
   *
   * @param book 书单
   * @return http 响应
   */
  @PostMapping("/books")
  public HttpEntity<?> booksAdd(@Valid @RequestBody Book book, BindingResult bindingResult) {
	book.setId(null);
	return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);
  }

  /**
   * 更新一个书单,提供一个书单的全部信息
   * PUT     /api/v1/books/{id}   更新一条书单，提供全部信息
   *
   * @param id 更新的id
   * @param book 更新后的书单
   * @return http 响应
   */
  @PutMapping("/books/{id}")
  public HttpEntity<?> booksPut(@Valid @PathVariable Long id, @RequestBody Book book, BindingResult bindingResult) {
	Book exist = bookRepository.findById(id).get();
	book.setId(exist.getId());
	return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
  }

  /**
   * 更新一个书单,提供一个书单的部分信息
   * PATCH   /api/v1/books/{id}   更新一条书单，提供部分信息
   *
   * @param id 更新的id
   * @param book 更新后的书单
   * @return http 响应
   */
  @PatchMapping("/books/{id}")
  public HttpEntity<?> booksPatch(@PathVariable Long id, @RequestBody Book book) {
	Book exist = bookRepository.findById(id).get();
	BeanWrapper beanWrapper = new BeanWrapperImpl(book);
	PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
	List<String> nullPropertyNames = new ArrayList<>();
	for (PropertyDescriptor pd : propertyDescriptors) {
	  if (beanWrapper.getPropertyValue(pd.getName()) == null) {
		 nullPropertyNames.add(pd.getName());
	  }
	}
	BeanUtils.copyProperties(book, exist, nullPropertyNames.toArray(new String[nullPropertyNames.size()]));
	return new ResponseEntity<>(bookRepository.save(exist), HttpStatus.OK);
  }

  /**
   * 删除一个书单
   * DELETE  /api/v1/books/{id}   删除一条书单
   *
   * @param id id
   * @return http 响应
   */
  @DeleteMapping("/books/{id}")
  public HttpEntity<?> booksDeleteOne(@PathVariable Long id) {
	Book exist = bookRepository.findById(id).get();
	bookRepository.deleteById(exist.getId());
	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * 删除所有书单
   * DELETE  /API/v1/books        删除所有书单
   *
   * @return http 响应
   */
  @DeleteMapping("/books")
  public HttpEntity<?> booksDeleteAll() {
	List<Book> books = bookRepository.findAll();
	bookRepository.deleteAll();
	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
```

4. 进行测试
![http://localhost:8080/api/v1/books 测试](https://resources.echocow.cn//file/2019/01/d43c2995597d45c3b3b32b828f647d75__sunawtX11XFramePeer_20190105221416.png)

其余的测试都是成功的,但是都是理想的情况,如果发生其他的情况呢?比如,我查询不存在书籍呢?

5. 进行错误测试:找不到资源

这个时候这个工具就不够用了,因为我们需要获取到他的状态码,所以我们需要使用 postman 了.
![找不到资源](https://resources.echocow.cn//file/2019/01/8d1da695d9fc491995f0c597804d03b7__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105221953.png)


6. 进行错误测试:字段不符合、

我们在 Book 的实体中的 name 字段加入了 `@NotNull` 注解,也就是非空验证。那么当客户端给的是错误的时候，会给出什么呢？
![字段不符合](https://resources.echocow.cn//file/2019/01/7e55b7e1ec144abb9c48ca1a9c0d0900__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105222450.png)

所以这就涉及到统一异常处理了。

## 第二步，统一异常处理

### 指定统一异常处理规范

现在我们遇到了两个问题，一个是 not found，应该给出 404，一个是 INVALID REQUEST，应该给出 400.所以他们应该相应返回如下

- 404
```json
status:404

data(可选):
{
  "msg" : "Not found books!"
}
```

- 400
```json
status:400

data(可选):
{
  "msg" : "invalid parameter",
  "errors": [
    {
	  "resource":"传过来的实体名称",
	  "field":"字段",
	  "code":"代码",
	  "message","信息"
	},{
	  "resource":"传过来的实体名称",
	  "field":"字段",
	  "code":"代码",
	  "message","信息"
	}
  ]
}
```

### 异常处理

1. 如果大家细心应该可以注意到在 controller 之中，idea 给我们报了很多警告，对于我来说是绝对不允许这些警告出现的，而这些警告也是提醒了我们的可能会出现的错误所在。

![controller](https://resources.echocow.cn//file/2019/01/7bb71108a4774046be61bb4305da6af9__sunawtX11XFramePeer_20190105222747.png)

_Optional_ 类 是 Java 8 新特性，是一个可以为null的容器对象。这里的提示的意思就是我们没有对获取到的 Optional 进行非空校验，校验他里面是否为空，这就是我们需要改进的地方。**解决办法很简单，就是判断，当他为空的时候，抛出一个异常即可。**所以我们需要自定义自己的异常信息。

2. 自定义异常

![ResourceNoFoundException](https://resources.echocow.cn//file/2019/01/07cca1aa09c94b73ad1c262415aff085__sunawtX11XFramePeer_20190105223726.png)

![InvalidRequestException](https://resources.echocow.cn//file/2019/01/3bd738cdd2db494ea4d885bfca86578c__sunawtX11XFramePeer_20190105223732.png)

3. 抛出异常

在可能出现异常的地方，抛出异常。

![抛出异常](https://resources.echocow.cn//file/2019/01/afa09c54e24e484a9661a465377fc640__sunawtX11XFramePeer_20190105224308.png)

同时可以看到，右侧的警告全都没了，太爽！消除警告原则！

4. 重启，进行测试

![进行测试](https://resources.echocow.cn//file/2019/01/6aca0ffcb1a94cc5a37693e693e18eb0__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105221953.png)

发现还是有点差距，这个就需要我们对响应进行封装了。我们查看控制台可以发现，抛出的使我们自定义的异常了。

### 封装错误信息

1. 我们需要添加几个信息封装的类，作为响应返回的实体

![ErrorResource](https://resources.echocow.cn//file/2019/01/5015e3b2d75d42edb0d8dce653e5d861__sunawtX11XFramePeer_20190105225300.png)

![FieldResource](https://resources.echocow.cn//file/2019/01/725a1d8e38864a4b918387c52db61db9__sunawtX11XFramePeer_20190105225306.png)

![InvalidErrorResource](https://resources.echocow.cn//file/2019/01/bf7c83e3e64d4b51bcadbabf5b3cc74a__sunawtX11XFramePeer_20190105225314.png)

2. 添加一个全局异常处理，用来拦截所有的异常信息并进行封装。

![拦截所有的异常信息并进行封装](https://resources.echocow.cn//file/2019/01/a9eb908088c942cca1633c2780422430__sunawtX11XFramePeer_20190105225717.png)

```java
package cn.echocow.restfulapi.handle;

import cn.echocow.restfulapi.exception.InvalidRequestException;
import cn.echocow.restfulapi.exception.ResourceNoFoundException;
import cn.echocow.restfulapi.resource.ErrorResource;
import cn.echocow.restfulapi.resource.FieldResource;
import cn.echocow.restfulapi.resource.InvalidErrorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 对异常进行拦截然后封装到响应体
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:59
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(ResourceNoFoundException.class)
  public HttpEntity<?> handleNotFound(ResourceNoFoundException e) {
    ErrorResource errorResource = new ErrorResource(e.getMessage());
    logger.error(errorResource.toString());
    return new ResponseEntity<>(errorResource, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public HttpEntity<?> handleInvalidRequest(InvalidRequestException e) {
    Errors errors = e.getErrors();
    List<FieldResource> fieldResources = new ArrayList<>();
    List<FieldError> fieldErrors = errors.getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      fieldResources.add(
          new FieldResource(fieldError.getObjectName(),
          fieldError.getField(),
          fieldError.getCode(),
          fieldError.getDefaultMessage())
      );
  }
    InvalidErrorResource invalidErrorResource = new InvalidErrorResource(e.getMessage(), fieldResources);
    logger.error(invalidErrorResource.toString());
    return new ResponseEntity<>(invalidErrorResource, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public HttpEntity<?> handleException(Exception e){
    logger.error(e.getMessage());
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
```

3. 进行测试

![出现错误](https://resources.echocow.cn//file/2019/01/25d2b143226c4ab8aac761b85e881432__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105221953.png)

![解决](https://resources.echocow.cn//file/2019/01/62ffcebf2c8642678321e148402dbca4___20190105231339.png)

4. 再次测试

![再次测试](https://resources.echocow.cn//file/2019/01/a142a8c51c14451a95dfd59b49251dea__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105231459.png)

这样就完成我们统一异常的处理。 第一版的 restful api 也就开发完毕～！当然，这只是一个简单的 restful api，为什么说他简单？那就是他缺少了一个  Hypermedia API！这是什么？可以访问 [github 的 restful api](https://api.github.com/) 就可以看到这么一个效果了。想要自己手动实现这个，自己能力还有些不足，不过欢迎大家交流学习。


# 使用 spring data rest

上面我们自己使用 spring boot 实现了一个 restful 的 api。我们从三层，变为了两层。不过前面提到了我们没有使用的的一个依赖，spring-boot-starter-data-rest，现在，我们就基于它，来开发一个 restful api，相信我，你会很惊讶的。

## 引入依赖

![_sunawtX11XFramePeer_20190105232540png](https://resources.echocow.cn//file/2019/01/025e4ac4161046de9560066b7c28977a__sunawtX11XFramePeer_20190105232540.png)

## 初体验

1. 然后你不需要修改任何代码，请直接访问 [http://localhost:8080/api/](http://localhost:8080/api/)

你会看到这么一个页面

![你会看到这么一个页面](https://resources.echocow.cn//file/2019/01/4c57e6b2abd2402a92078388a507dd39___20190105232804.png)

2. 然后你试着访问他给你的两个链接看看

![然后你试着访问他给你的两个链接看看](https://resources.echocow.cn//file/2019/01/f283fe5780934066845334c3c1c6c157___20190105233750.png)

完美+2！同时还给出了我们需要的 Hypermedia API！

**不过值得注意，他的路径没有 v1 了**

3. 测试一下 api

![get](https://resources.echocow.cn//file/2019/01/db537f6b37a84cef9bef90c4fdb578a6__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235043.png)

![get](https://resources.echocow.cn//file/2019/01/e742dcb8cd444b5b8eb3b7a173ff1f84__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235058.png)


![put](https://resources.echocow.cn//file/2019/01/1e2850ef657a4f1ba173992ce22d82da__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235115.png)

![delete](https://resources.echocow.cn//file/2019/01/40990ce9fc904c1ebdec9994c71add4b__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235127.png)

4. 测试一下异常情况

![404](https://resources.echocow.cn//file/2019/01/f0f8d81b5b8744a2920087b2e6077e6c__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235259.png)

![400](https://resources.echocow.cn//file/2019/01/58afe28634924c7080c052f97bd59534__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190105235337.png)

出现了意外状况，400 的期望，来了 500。。如何处理呢？在这之前，我们了解下吧。


## 发生了什么

我，，，我也不知道啊=-=我就加了一个依赖，然后只要带有 `@Repository` 注解且继承了 `Repository` 及其他的子接口的接口的方法都暴露出去了。至于为什么我也不清楚，因为应该是使用了 `@RepositoryRestResource` 注解的才应该会被暴露出去，我到现在还不能明白。这就是为啥前面要大家暂时不用那个依赖的原因。不过不碍事，我们继续。

## 添加一个 v2 版本的 restful

1. 添加 BookRestRepository

![BookRestRepository](https://resources.echocow.cn//file/2019/01/c779af7e12e74a28869b9aea2c1e1a59__sunawtX11XFramePeer_20190106012444.png)

2. 设置基础路径

![设置基础路径](https://resources.echocow.cn//file/2019/01/4610cd26fe99456c8d39b390055e8c92__sunawtX11XFramePeer_20190106012738.png)

3. 测试访问

![测试访问](https://resources.echocow.cn//file/2019/01/653c4c84167247d79adb7d78cecbc300__crx_fhbjgbiflinjbdggehcddcbncdddomop_20190106013118.png)

## 关于 spring data rest
神奇的在于他的注解，关于 rest 的注解主要有四个

1. @RestController              完全自定义控制器，完全交由自己处理
2. @RepositoryRestResource      完全使用已设置的Spring Data REST配置，不需要自定义控制
3. @RepositoryRestController    希望使用已设置的Spring Data REST配置，但是部分需要自定义
4. @BasePathAwareController     如果您对特定于实体的操作不感兴趣但仍希望在basePath下构建自定义操作，例如Spring MVC视图，资源等，请使用@BasePathAwareController（资料太少完全没人用的感觉）

如果完全使用 spring data rest 进行处理就会暴露出我们继承的接口的方法。对于 Repository 接口主要有三个子接口，分别是 CrudRepository、 JpaRepository、PagingAndSortingRepository,他们的关系如下

![__20190108095946png](https://resources.echocow.cn//file/2019/01/df8271d33a7b48a191bbce456d70e015___20190108095946.png)

可以看到， CrudRepository 提供基础的 增删改查，PagingAndSortingRepository 又提供了分页和排序，JpaRepository 多继承了一个 QueryByExampleExecutor，用来对 QBE 的支持，对于 restful api 来说，只需继承 PagingAndSortingRepository 即可。



### 处理参数校验

前面我们测试了一下，如果我们的参数不合法，比如名称为null，他直接返回 500 的错误，我们期望的是 400 bad request，那么我们如何修改呢？传统的controller-service-dao模式中，处理业务数据时，可以在service或者controller中处理，但是使用Spring data rest时，由于框架自己生成相关接口，处理相关业务就要实现监听才行。有多种方式实现监听操作，我选择其中一种进行演示：通过实现 RepositoryRestConfigurer 进行参数校验

1. 创建 BookValidator ，实现 Validator 接口

![BookValidator](https://resources.echocow.cn//file/2019/01/7343feb284ed45b38a0b4472a94e71fc__sunawtX11XFramePeer_20190108111013.png)

2. 创建 RepositoryRestConfig，实现 RepositoryRestConfigurer 接口，覆盖 configureValidatingRepositoryEventListener 方法

![RepositoryRestConfig](https://resources.echocow.cn//file/2019/01/9cf30d72db5f4047917956de98355eb9__sunawtX11XFramePeer_20190108111115.png)

3. 让 BookRestRepository 继承 PagingAndSortingRepository

![BookRestRepository](https://resources.echocow.cn//file/2019/01/5a156227af6a4c469831d92f4fe2bf9d__sunawtX11XFramePeer_20190108111216.png)

4. 运行测试

![运行测试](https://resources.echocow.cn//file/2019/01/3fe843e3490a45c5937958b0f7a4870c__sunawtX11XFramePeer_20190108111359.png)

5. 对比异常

![对比异常](https://resources.echocow.cn//file/2019/01/391e8d141c7948fc882776b478ddbb6d__sunawtX11XFramePeer_20190108111754.png)

6. 那么接下来就好办了，我们处理的异常的方式就和我们处理 InvalidRequestException 异常的方式一样的了。为什么一开始不直接用它的 RepositoryConstraintViolationException 呢？因为他是属于 spring data rest 下的，前面我们并没有引入这个包，所以不能使用，现在引入了，自然可以使用了，并且可以删除我们以前的那个 InvalidRequestException（我暂时不删除）

![处理异常](https://resources.echocow.cn//file/2019/01/5afdff176e4e42278ea8f5124fa29f79__sunawtX11XFramePeer_20190108112257.png)

6. 再次测试

![再次测试](https://resources.echocow.cn//file/2019/01/9bf9be7e0c2a4553895183e518a76768__sunawtX11XFramePeer_20190108112543.png)

7. 查错

![查错](https://resources.echocow.cn//file/2019/01/dd99a3d9cd9a4505ad4cb204a0f6e78f__sunawtX11XFramePeer_20190108112842.png)

8. 再次测试

![再次测试](https://resources.echocow.cn//file/2019/01/67587e8fbd004abeac1563917d6d0259__sunawtX11XFramePeer_20190108112936.png)


9. 这样就完成了，那么测试一下更新的时候呢？

![测试一下更新](https://resources.echocow.cn//file/2019/01/e01fb9efe8294a67a51f2aa3955c0902__sunawtX11XFramePeer_20190108113149.png)

这样一个参数的校验和异常处理就完成了！

### 方法自定义

我们在实际时候，有很多他的方法我们是不希望暴露出来的，比如删除方法，如果我们不希望暴露出来，怎么办呢？

#### 隐藏方法

很简单，一个注解搞定！
![隐藏方法](https://resources.echocow.cn//file/2019/01/0a07ae1ca6d9464fa21f6496c38f0f5f__sunawtX11XFramePeer_20190108113858.png)

**为什么这里会报 500 错误，这里其实并不需要我们再次进行处理，其原因在于我们配置的全局异常处理导致的**

![统一异常处理](https://resources.echocow.cn//file/2019/01/f40896d2e65749fd9ec4d8ff89c05431__sunawtX11XFramePeer_20190108114706.png)

所以处理方式很简单，指定一下他要拦截的 controller 即可

![controller](https://resources.echocow.cn//file/2019/01/46e268e8939f41cda577ea7b77be9e38__sunawtX11XFramePeer_20190108115504.png)

![测试](https://resources.echocow.cn//file/2019/01/107c475d2e3c4565b07d73f62e7f1395__sunawtX11XFramePeer_20190108115558.png)

![测试](https://resources.echocow.cn//file/2019/01/2db1f9635871403eac953e1d815db874__sunawtX11XFramePeer_20190108115810.png)


#### 修改方法

但是在实际开发中，删除是要的有的，但是我们一般并不是真正的删除数据，而是通过修改他的 isEnabled 或者 status 达到删除的目的，这个时候就要我们自定义删除方法了。

![修改方法](https://resources.echocow.cn//file/2019/01/75dc5e3d85df4de9b90344a4988defde__sunawtX11XFramePeer_20190108123753.png)

#### 隐藏字段

查询的数据中，把实体的所有属性查出来了，那么我们要怎么隐藏其中的属性呢？很简单，一个注解即可

![_sunawtX11XFramePeer_20190108124021png](https://resources.echocow.cn//file/2019/01/42576f7dcb054844ab05b84cdde36e97__sunawtX11XFramePeer_20190108124021.png)

#### 自定义查询方法

一种简单的实现就是直接利用 jpa，然后暴露出去即可，如下：
![然后暴露出去即可](https://resources.echocow.cn//file/2019/01/3b9994f7f7c74f7e8aba058f7b6f2cce__sunawtX11XFramePeer_20190108142720.png)

![然后暴露出去即可](https://resources.echocow.cn//file/2019/01/24345e49bf3f4eb8ae3bc6f8ee016744__sunawtX11XFramePeer_20190108142947.png)

当然，这样你会发现他的url就是带有参数的了，这样也可以的。但是如果不想这样呢？比如根据作者来查询我希望的 url 是 `/api/v2/books/authors/{name}`，那么就要用到扩充了。也就是 `@RepositoryRestController` 注解，当然你也可以直接使用 `@RestController` 注解的。然后在里面添加方法即可，我就不再赘述啦！

# 关于测试
我们前面使用到了 2 种测试，使用 idea 的插件，功能有限，还有就是使用强大的 postman，那么如何使用 spring boot 的 test 测试呢？

在这之前确保你已经安装了如下依赖

```
testImplementation('org.springframework.boot:spring-boot-starter-test')
```

他自带的是 junit4，你也可以使用 junit5，也是非常方便的。现在我们先使用 junit4 进行测试。

![_sunawtX11XFramePeer_20190108144243png](https://resources.echocow.cn//file/2019/01/99648fea469047efae037dab43971bae__sunawtX11XFramePeer_20190108144243.png)



这样我们便完成了一个接口的测试，你可以通过 `andExpect` 详细的测试关于获取到的json对象的信息，你也可以接受一个返回值后进行打印查看。

如果使用 junit5 呢？大体相同的

![_sunawtX11XFramePeer_20190108144503png](https://resources.echocow.cn//file/2019/01/8424aa58232e42a98d93f3c77f35063f__sunawtX11XFramePeer_20190108144503.png)



不过要注意的是，因为在应用内进行测试，所以我们不需要添加 `/api` 了。

> 为什么要这样？直接用 postman 不好吗？开发的时候，我们可以使用 postman 一个一个测试，但是如果你想一整套的演示测试，这样一个一个的请求一个个测试是不是很麻烦？所以你可以将他直接书写到一个类中，比如书写一个 BookV1Test 类，然后开发完 V1 版本的，直接运行整个类，他会依次运行所有的方法，并且由于我们前面配置了测试时数据固定，不可变了，所以你可以对所有的获取到的数据进行预测，每次测试时运行的数据都是一致的，那么你就可以观察是否得到期望的值。一次性可以测试完所有的接口，是非常方便的（好像postman也有）。好就好在别人拿代码过去可以直接运行测试类查看结果了。但是我比较懒所以只写一个，大家知道就行了。

# 参考链接
- [理解 RESTFul 架构  阮一峰](http://www.ruanyifeng.com/blog/2011/09/restful.html)
- [RESTful API 设计指南  阮一峰](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)
- [RESTful API 最佳实践  阮一峰](http://www.ruanyifeng.com/blog/2018/10/restful-api-best-practices.html)
- [spring data restful](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)