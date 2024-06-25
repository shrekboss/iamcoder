## 命令模式

> 手游后端服务器轮询获取客户端发来的请求，获取到请求之后，借助命令模式，把请求包含的数据和处理逻辑封装为命令对象，并存储在内存队列中。然后，再从队列中取出一定数量的命令来执行。执行完成之后，再重新开始新的一轮轮询。

具体的示例代码如下:

- [Command.java](Command.java)
  - [ArchiveCommand.java](ArchiveCommand.java)
  - [GotDiamondCommand.java](GotDiamondCommand.java)
  - [GotStartCommand.java](GotStartCommand.java)
  - [HitObstacleCommand.java](HitObstacleCommand.java)
- [GameApplication.java](GameApplication.java)
