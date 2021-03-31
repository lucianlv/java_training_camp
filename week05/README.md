- 修复本程序 org.geektimes.reactive.streams 包下程序
- 继续完善 my-rest-client POST 方法
- (可选) 读一下 Servlet 3.0 关于 Servlet 异步
  - AsyncContext

作业提交链接：[ https://jinshuju.net/f/DRe5aZ](https://jinshuju.net/f/DRe5aZ)

---

### 1. 修复本程序 org.geektimes.reactive.streams 包下程序

先输出收到数据，在处理cancel逻辑

```java
    @Override
    public void onNext(Object o) {
        System.out.println("收到数据：" + o);

        if (count++ > 2) { // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
            subscription.cancel();
            return;
        }

    }
```



### 2. 继续完善 my-rest-client POST 方法

完成 org.geektimes.rest.client.HttpPostInvocation

实现 org.geektimes.rest.client.DefaultInvocationBuilder#buildPost

验证 org.geektimes.rest.demo.RestClientDemo