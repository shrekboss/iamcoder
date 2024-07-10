## Pipeline 模式的可复用实现代码

参考代码：

- [AbstractParallelPipe.java](AbstractParallelPipe.java)
- [Pipe.java](Pipe.java)
    - [AbstractPipe.java](AbstractPipe.java)
        - [SimplePipeline.java](SimplePipeline.java)
    - [Pipeline.java](Pipeline.java)
    - [WorkerThreadPipeDecorator.java](WorkerThreadPipeDecorator.java)
- [PipeContext.java](PipeContext.java)
- [PipeException.java](PipeException.java)
- [ThreadPoolPipeDecorator.java](ThreadPoolPipeDecorator.java)

利用可复用代码实现 Pipeline 模式，应用程序只需要完成以下几件事情：

1. 【必需】创建 Pipeline 实例。
2. 【必需】创建表示各个处理阶段的 Pipe 实例，并将其添加到 Pipeline 实例中。
3. 【必需】初始化 Pipeline 实例。
