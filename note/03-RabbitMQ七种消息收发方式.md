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

一个队列对应了多个消费者，默认情况下，由队列对消息进行平均分配，消息会被分到不同的消费者手中。消费者可以配置各自的并发能力，进而提高消息的消费能力，也可以配置手动 ack，来决定是否要消费某一条消息。

先来看并发能力的配置，如下：

```java
@Component
public class HelloWorldConsumer {
    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME)
    public void receive(String msg) {
        System.out.println("receive = " + msg);
    }
    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME,concurrency = "10")
    public void receive2(String msg) {
        System.out.println("receive2 = " + msg+"------->"+Thread.currentThread().getName());
    }
}
```

可以看到，第二个消费者我配置了 concurrency 为 10，此时，对于第二个消费者，将会同时存在 10 个子线程去消费消息。

启动项目，在 RabbitMQ 后台也可以看到一共有 11 个消费者。

此时，如果生产者发送 10 条消息，就会一下都被消费掉。

消息发送方式如下：

```java
@SpringBootTest
class RabbitmqdemoApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(HelloWorldConfig.HELLO_WORLD_QUEUE_NAME, "hello");
        }
    }

}
```

消息都被第一个消费者消费了。但是小伙伴们需要注意，事情并不总是这样（多试几次就可以看到差异），消息也有可能被第一个消费者消费（只是由于第二个消费者有十个线程一起开动，所以第二个消费者消费的消息占比更大）。

当然消息消费者也可以开启手动 ack，这样可以自行决定是否消费 RabbitMQ 发来的消息，配置手动 ack 的方式如下：

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=manual
```

消费代码如下：

```java
@Component
public class HelloWorldConsumer {
    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME)
    public void receive(Message message,Channel channel) throws IOException {
        System.out.println("receive="+message.getPayload());
        channel.basicAck(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)),true);
    }

    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME, concurrency = "10")
    public void receive2(Message message, Channel channel) throws IOException {
        System.out.println("receive2 = " + message.getPayload() + "------->" + Thread.currentThread().getName());
        channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }
}
```

此时第二个消费者拒绝了所有消息，第一个消费者消费了所有消息。

当我们重启应用的时候，还会重新发送10条消息，这是因为开起来手动确认ack，生产者无法确认消费者是否消费成功，需要手动确认：

需要在参数位置修改为：Message和Channel

> 确认消费：
>
> ​	channel.basicAck(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)),true);
>
> ​    
>
> 拒绝消费：
>
> ​    channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);

```java
    @RabbitListener(queues = HelloWorldConfig.HELLO_WORLD_QUEUE_NAME, concurrency = "10")
    public void receive2(Message message, Channel channel) throws IOException {
        System.out.println("receive2 = " + message.getPayload() + "------->" + Thread.currentThread().getName());
        channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }
```



这就是 Work queues 这种情况。

#### 3.3.3 Publish/Subscribe

再来看发布订阅模式，这种情况是这样：

一个生产者，多个消费者，每一个消费者都有自己的一个队列，生产者没有将消息直接发送到队列，而是发送到了交换机，每个队列绑定交换机，生产者发送的消息经过交换机，到达队列，实现一个消息被多个消费者获取的目的。需要注意的是，如果将消息发送到一个没有队列绑定的 Exchange上面，那么该消息将会丢失，这是因为在 RabbitMQ 中 Exchange 不具备存储消息的能力，只有队列具备存储消息的能力，如下图：

这种情况下，我们有四种交换机可供选择，分别是：

- Direct
- Fanout
- Topic
- Header

##### 3.3.3.1 Direct

DirectExchange 的路由策略是**将消息队列绑定到一个 DirectExchange 上，当一条消息到达 DirectExchange 时会被转发到与该条消息 routing key 相同的 Queue 上**，例如消息队列名为 “direct_queue_name”，则 routingkey 为 “direct_queue_name” 的消息会被该消息队列接收。DirectExchange 的配置如下：

```java
/**
 * Direct:这种路由策略，将消息队列绑定到 DirectExchange 上，当消息到达交换机的时候，消息会携带 routing_key,交换机会找到名为 routing_key 的队列，将消息路由过去
 */
@Configuration
public class RabbitConfig {

    public static final String DIRECT_QUEUE_NAME = "direct_queue_name";
    public static final String DIRECT_QUEUE_NAME2 = "direct_queue_name2";

    public static final String DIRECT_EXCHANGE_NAME = "direct_exchange_name";


    @Bean
    Queue directQueue1(){
        return new Queue(DIRECT_QUEUE_NAME,true,false,false);
    }

    @Bean
    Queue directQueue2(){
        return new Queue(DIRECT_QUEUE_NAME2,true,false,false);
    }

    /**
     * 定义一个直连交换机
     * @return
     */
    @Bean
    DirectExchange directExchange(){
        //1.交换机名称
        //2.交换机本身是否持久化
        //3.如果没有与之绑定的队列，是否删除交换机
        return new DirectExchange(DIRECT_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding directBinding1(){
        return BindingBuilder
                //设置绑定的队列
                .bind(directQueue1())
                //设置绑定的交换机
                .to(directExchange())
                //设置 routing_key
                .with(DIRECT_QUEUE_NAME);
    }

    @Bean
    Binding directBinding2(){
        return BindingBuilder
                //设置绑定的队列
                .bind(directQueue2())
                //设置绑定的交换机
                .to(directExchange())
                //设置 routing_key
                .with(DIRECT_QUEUE_NAME2);
    }

}
```

- 首先提供了两个队列 Queue，然后创建一个 DirectExchange 对象，三个参数分别是名字，重启后是否依然有效，以及长期未使用时是否删除
- 创建两个 Binding 对象将两个队列绑定到交换机上

再来看消费者：

```java
@Component
public class MsgReceive {

    @RabbitListener(queues = RabbitConfig.DIRECT_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("消息内容1 = " + message);
    }

    @RabbitListener(queues = RabbitConfig.DIRECT_QUEUE_NAME2)
    public void handleMsg2(String message) throws IOException {
        System.out.println("消息内容2 = " + message);

    }

}
```



通过@RabbitListener 注解指定一个方法是一个消息消费方法，方法参数就是所接收到的消息。不同方法监听不同的队列，然后在单元测试类中注入一个 RabbitTemplate 对象来进行消息发送，如下：

```java
@SpringBootTest
class PublishApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
            rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE_NAME,RabbitConfig.DIRECT_QUEUE_NAME,"这条消息发给队列1");
            rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE_NAME,RabbitConfig.DIRECT_QUEUE_NAME2,"这条消息发给队列2");
    }

}
```

##### 3.3.3.2 Fanout

FanoutExchange 的数据交换策略是把所有到达 FanoutExchange 的消息转发给所有与它绑定的 Queue 上，在这种策略中，routingkey 将不起任何作用，FanoutExchange 配置方式如下：

```java
/**
 * Fanout 交换机会将到达交换机的所有消息路由到与它绑定的所有队列上来
 */
@Configuration
public class FanoutConfig {

    public static final String FANOUT_QUEUE_NAME = "fanout_queue_name";
    public static final String FANOUT_QUEUE_NAME2 = "fanout_queue_name2";
    public static final String FANOUT_EXCHANGE_NAME = "fanout_exchange_name";

    @Bean
    Queue fanoutQueue1(){
        return new Queue(FANOUT_QUEUE_NAME,true,false,false);
    }

    @Bean
    Queue fanoutQueue2(){
        return new Queue(FANOUT_QUEUE_NAME2,true,false,false);
    }

    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding binding1(){
        return BindingBuilder.bind(fanoutQueue1())
                .to(fanoutExchange());
    }

    @Bean
    Binding binding2(){
        return BindingBuilder.bind(fanoutQueue2())
                .to(fanoutExchange());
    }

}
```

在这里首先创建 FanoutExchange，参数含义与创建 DirectExchange 参数含义一致，然后创建两个 Queue，再将这两个 Queue 都绑定到 FanoutExchange 上。接下来创建两个消费者，如下：

```java
@Component
public class FanoutReceive {

    @RabbitListener(queues = FanoutConfig.FANOUT_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("FANOUT消息内容1 = " + message);
    }

    @RabbitListener(queues = FanoutConfig.FANOUT_QUEUE_NAME2)
    public void handleMsg2(String message) throws IOException {
        System.out.println("FANOUT消息内容2 = " + message);
    }

}
```

两个消费者分别消费两个消息队列中的消息，然后在单元测试中发送消息，如下：

```java
@Test
void fanout() {
    rabbitTemplate.convertAndSend(FanoutConfig.FANOUT_EXCHANGE_NAME,null,"这条消息发给队列");
}
```

会发现两个队列都收到了消息。

##### 3.3.3.3 Topic

TopicExchange 是比较复杂但是也比较灵活的一种路由策略，在 TopicExchange 中，Queue 通过 routingkey 绑定到 TopicExchange 上，当消息到达 TopicExchange 后，TopicExchange 根据消息的 routingkey 将消息路由到一个或者多个 Queue 上。TopicExchange 配置如下：

```java
@Configuration
public class TopicConfig {
    public static final String XIAOMI_QUEUE_NAME = "xiaomi_queue_name";
    public static final String HUAWEI_QUEUE_NAME = "huawei_queue_name";
    public static final String PHONE_QUEUE_NAME = "phone_queue_name";
    public static final String TOPIC_EXCHANGE_NAME = "xiaomi_queue_name";

    @Bean
    Queue xiaomiQueue(){
        return new Queue(XIAOMI_QUEUE_NAME,true,false,false);
    }

    @Bean
    Queue huaweiQueue(){
        return new Queue(HUAWEI_QUEUE_NAME,true,false,false);
    }

    @Bean
    Queue phoneQueue(){
        return new Queue(PHONE_QUEUE_NAME,true,false,false);
    }

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding xiaomiBing(){
        return BindingBuilder.bind(xiaomiQueue())
                .to(topicExchange())
                // 这里的 # 是一个通配符，表示将来消息的 routing_key 只要以 xiaomi 开头，都将被路由到 xiaomiQueue 上来
                .with("xiaomi.#");
    }

    @Bean
    Binding huaweiBing(){
        return BindingBuilder.bind(huaweiQueue())
                .to(topicExchange())
                // 这里的 # 是一个通配符，表示将来消息的 routing_key 只要以 xiaomi 开头，都将被路由到 xiaomiQueue 上来
                .with("huawei.#");
    }

    @Bean
    Binding phoneBing(){
        return BindingBuilder.bind(phoneQueue())
                .to(topicExchange())
                // 这里的 # 是一个通配符，表示将来消息的 routing_key 只要以 xiaomi 开头，都将被路由到 xiaomiQueue 上来
                .with("#.phone.#");
    }
}
```

- 首先创建 TopicExchange，参数和前面的一致。然后创建三个 Queue，第一个 Queue 用来存储和 “xiaomi” 有关的消息，第二个 Queue 用来存储和 “huawei” 有关的消息，第三个 Queue 用来存储和 “phone” 有关的消息。
- 将三个 Queue 分别绑定到 TopicExchange 上，第一个 Binding 中的 “xiaomi.#” 表示消息的 routingkey 凡是以 “xiaomi” 开头的，都将被路由到名称为 “xiaomi” 的 Queue 上，第二个 Binding 中的 “huawei.#” 表示消息的 routingkey 凡是以 “huawei” 开头的，都将被路由到名称为 “huawei” 的 Queue 上，第三个 Binding 中的 “#.phone.#” 则表示消息的 routingkey 中凡是包含 “phone” 的，都将被路由到名称为 “phone” 的 Queue 上。

接下来针对三个 Queue 创建三个消费者，如下：

```java
@Component
public class TopicReveive {
    @RabbitListener(queues = TopicConfig.XIAOMI_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("TOPIC消息内容1 = " + message);
    }

    @RabbitListener(queues = TopicConfig.HUAWEI_QUEUE_NAME)
    public void handleMsg2(String message) throws IOException {
        System.out.println("TOPIC消息内容2 = " + message);
    }

    @RabbitListener(queues = TopicConfig.PHONE_QUEUE_NAME)
    public void handleMsg3(String message) throws IOException {
        System.out.println("TOPIC消息内容3 = " + message);
    }
}
```

然后在单元测试中进行消息的发送，如下：

```java
@Test
void topic() {
    rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"xiaomi.news","小米新闻");
    rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"huawei.news","华为");
    rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"huawei.phone.news","华为手机新闻");
}
```

根据 RabbitTopicConfig 中的配置，第一条消息将被路由到名称为 “xiaomi” 的 Queue 上，第二条消息将被路由到名为 “huawei” 的 Queue 上，第三条消息将被路由到名为 “xiaomi” 以及名为 “phone” 的 Queue 上，第四条消息将被路由到名为 “huawei” 以及名为 “phone” 的 Queue 上，最后一条消息则将被路由到名为 “phone” 的 Queue 上。