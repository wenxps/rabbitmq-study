大部分情况下，我们可能都是在 Spring Boot 或者 Spring Cloud 环境下使用 RabbitMQ，因此本文我也主要从这两个方面来和大家分享 RabbitMQ 的用法。3.1 RabbitMQ 架构简介

一图胜千言，如下：

![图片](https://mmbiz.qpic.cn/mmbiz_png/GvtDGKK4uYkQuwiab3o4x3ZE1ugPGl57ZsLOQAWvXCKmag8rlBomSxnWSsRb1iaZXOAicy51iaEbCsiaK6Pr8GkC2yg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)1587705504342

这张图中涉及到如下一些概念：

1. 生产者（Publisher）：发布消息到 RabbitMQ 中的交换机（Exchange）上。
2. 交换机（Exchange）：和生产者建立连接并接收生产者的消息。
3. 消费者（Consumer）：监听 RabbitMQ 中的 Queue 中的消息。
4. 队列（Queue）：Exchange 将消息分发到指定的 Queue，Queue 和消费者进行交互。
5. 路由（Routes）：交换机转发消息到队列的规则。

### 3.1 RabbitMQ 架构简介

一图胜千言，如下：

![图片](D:/dev/local_blog/消息队列/01.RabbitMQ教程/640-165391329449310.png)

这张图中涉及到如下一些概念：

1. 生产者（Publisher）：发布消息到 RabbitMQ 中的交换机（Exchange）上。
2. 交换机（Exchange）：和生产者建立连接并接收生产者的消息。
3. 消费者（Consumer）：监听 RabbitMQ 中的 Queue 中的消息。
4. 队列（Queue）：Exchange 将消息分发到指定的 Queue，Queue 和消费者进行交互。
5. 路由（Routes）：交换机转发消息到队列的规则。

### 3.2 准备工作

大家知道，RabbitMQ 是 AMQP 阵营里的产品，Spring Boot 为 AMQP 提供了自动化配置依赖 spring-boot-starter-amqp，因此首先创建 Spring Boot 项目并添加该依赖:

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

项目创建成功后，在 application.properties 中配置 RabbitMQ 的基本连接信息，如下：

```java
spring.rabbitmq.host=localhost
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.port=5672
```

接下来进行 RabbitMQ 配置，在 RabbitMQ 中，所有的消息生产者提交的消息都会交由 Exchange 进行再分配，Exchange 会根据不同的策略将消息分发到不同的 Queue 中。

RabbitMQ 官网介绍了如下几种消息分发的形式：



![图片](D:/dev/local_blog/%25E6%25B6%2588%25E6%2581%25AF%25E9%2598%259F%25E5%2588%2597/01.RabbitMQ%25E6%2595%2599%25E7%25A8%258B/640-16539208360672.png)![图片](03-RabbitMQ%E4%B8%83%E7%A7%8D%E6%B6%88%E6%81%AF%E6%94%B6%E5%8F%91%E6%96%B9%E5%BC%8F.assets/640-16539208360673.png)

这里给出了七种，其中第七种是消息确认，消息确认这块松哥之前发过相关的文章，传送门：

- [四种策略确保 RabbitMQ 消息发送可靠性！你用哪种？](https://mp.weixin.qq.com/s?__biz=MzI1NDY0MTkzNQ==&mid=2247494501&idx=1&sn=82de6d7ab3b18c5aa5ed59dcacff540a&scene=21#wechat_redirect)
- [RabbitMQ 高可用之如何确保消息成功消费](https://mp.weixin.qq.com/s?__biz=MzI1NDY0MTkzNQ==&mid=2247494749&idx=1&sn=5a26f75a88fdd95081b2302faa76d62f&scene=21#wechat_redirect)

所以这里我主要和大家介绍前六种消息收发方式。

### 3.3 消息收发

#### 3.3.1 Hello World

咦？这个咋没有交换机？这个其实是默认的交换机，我们需要提供一个生产者一个队列以及一个消费者。消息传播图如下：

![图片](03-RabbitMQ%E4%B8%83%E7%A7%8D%E6%B6%88%E6%81%AF%E6%94%B6%E5%8F%91%E6%96%B9%E5%BC%8F.assets/640-16539208360674.png)

来看看代码实现：

先来看看队列的定义：

```java
@Configuration
public class HelloWorldConfig {

    public static final String HELLO_WORLD_QUEUE_NAME = "hello_world_queue";

    @Bean
    Queue queue1() {
        return new Queue(HELLO_WORLD_QUEUE_NAME);
    }
}
```

再来看看消息消费者的定义：

```java
@Component
public class HelloWorldConsumer {
    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME)
    public void receive(String msg) {
        System.out.println("msg = " + msg);
    }
}
```

消息发送：

```java
@SpringBootTest
class RabbitmqdemoApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Test
    void contextLoads() {
        rabbitTemplate.convertAndSend(HelloWorldConfig.HELLO_WORLD_QUEUE_NAME, "hello");
    }

}
```

这个时候使用的其实是默认的直连交换机（DirectExchange），DirectExchange 的路由策略是将消息队列绑定到一个 DirectExchange 上，当一条消息到达 DirectExchange 时会被转发到与该条消息 `routing key` 相同的 Queue 上，例如消息队列名为 “hello-queue”，则 routingkey 为 “hello-queue” 的消息会被该消息队列接收。

#### 3.3.2 Work queues

这种情况是这样的：

一个生产者，一个默认的交换机（DirectExchange），一个队列，两个消费者。

