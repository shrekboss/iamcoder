## Half-sync/Half-async 模式的可复用实现代码

参考代码：

- [AsyncTask.java](AsyncTask.java)
- [SampleAsyncTask.java](SampleAsyncTask.java)

1. 【必需】定义一个含义具体的服务方法名，该方法可直接调用父类的 dispatch 方法。
2. 【必需】实现父类的抽象方法 doInBackground 方法。
3. 【必需】根据应用的实际需要覆盖父类的 onPreExecute 方法、onPostExecute 方法和 onExecutionException 方法。
