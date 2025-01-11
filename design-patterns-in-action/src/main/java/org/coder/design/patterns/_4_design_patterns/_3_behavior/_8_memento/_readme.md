## 备忘录模式

> 假设有这样一道面试题，希望你编写一个小程序，可以接收命令行的输入。用户输入文本时，程序将其追加存储在内存文本中；用户输入“:
> list”，程序在命令行中输出内存文本的内容；用户输入“:undo”，程序会撤销上一次输入的文本，也就是从内存文本中将上次输入的文本删除掉。

```text
>hello
>:list
hello
>world
>:list
helloworld
>:undo
>:list
hello
```

代码实现如下：

```java
public class InputText {
  private StringBuilder text = new StringBuilder();

  public String getText() {
    return text.toString();
  }

  public void append(String input) {
    text.append(input);
  }

  public void setText(String text) {
    this.text.replace(0, this.text.length(), text);
  }
}

public class SnapshotHolder {
  private Stack<InputText> snapshots = new Stack<>();

  public InputText popSnapshot() {
    return snapshots.pop();
  }

  public void pushSnapshot(InputText inputText) {
    InputText deepClonedInputText = new InputText();
    deepClonedInputText.setText(inputText.getText());
    snapshots.push(deepClonedInputText);
  }
}

public class ApplicationMain {
  public static void main(String[] args) {
    InputText inputText = new InputText();
    SnapshotHolder snapshotsHolder = new SnapshotHolder();
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      String input = scanner.next();
      if (input.equals(":list")) {
        System.out.println(inputText.getText());
      } else if (input.equals(":undo")) {
        InputText snapshot = snapshotsHolder.popSnapshot();
        inputText.setText(snapshot.getText());
      } else {
        snapshotsHolder.pushSnapshot(inputText);
        inputText.append(input);
      }
    }
  }
}
```

- 为了能用快照恢复 InputText 对象，在 InputText 类中定义了 setText() 函数，但这个函数有可能会被其他业务使用，所以，暴露不应该暴露的函数违背了封装原则；
- 快照本身是不可变的，理论上讲，不应该包含任何 set() 等修改内部状态的函数，但在上面的代码实现中，“快照“这个业务模型复用了
  InputText 类的定义，而 InputText 类本身有一系列修改内部状态的函数，所以，用 InputText 类来表示快照违背了封装原则。

- 针对以上问题，我们对代码做两点修改
    - 定义一个独立的类（Snapshot 类）来表示快照，而不是复用 InputText 类。这个类只暴露 get() 方法，没有 set() 等任何修改内部状态的方法。
    - 在 InputText 类中，把 setText() 方法重命名为 restoreSnapshot() 方法，用意更加明确，只用来恢复对象。