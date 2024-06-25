## 中介模式

### 案例一：登录、注册 UI 界面

原始代码如下：

```java
import javax.swing.text.View;
import java.awt.*;

public class UIControl {
    private static final String LOGIN_BTN_ID = "login_btn";
    private static final String REG_BTN_ID = "reg_btn";
    private static final String USERNAME_INPUT_ID = "username_input";
    private static final String PASSWORD_INPUT_ID = "pswd_input";
    private static final String REPEATED_PASSWORD_INPUT_ID = "repeated_pswd_input";
    private static final String HINT_TEXT_ID = "hint_text";
    private static final String SELECTION_ID = "selection";

    public static void main(String[] args) {
        Button loginButton = (Button) findViewById(LOGIN_BTN_ID);
        Button regButton = (Button) findViewById(REG_BTN_ID);
        Input usernameInput = (Input) findViewById(USERNAME_INPUT_ID);
        Input passwordInput = (Input) findViewById(PASSWORD_INPUT_ID);
        Input repeatedPswdInput = (Input) findViewById(REPEATED_PASSWORD_INPUT_ID);
        Text hintText = (Text) findViewById(HINT_TEXT_ID);
        Selection selection = (Selection) findViewById(SELECTION_ID);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.text();
                String password = passwordInput.text();
                //校验数据...
                //做业务处理...
            }
        });

        regButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取usernameInput、passwordInput、repeatedPswdInput数据...
                //校验数据...
                //做业务处理...
            }
        });

        //...省略selection下拉选择框相关代码....
    }
}
```

按照中介模式，将上面的代码重新实现一下，参考代码如下:

```java
import javax.swing.text.View;
import java.awt.*;

public interface Mediator {
    void handleEvent(Component component, String event);
}

public class LandingPageDialog implements Mediator {
    private Button loginButton;
    private Button regButton;
    private Selection selection;
    private Input usernameInput;
    private Input passwordInput;
    private Input repeatedPswdInput;
    private Text hintText;

    @Override
    public void handleEvent(Component component, String event) {
        if (component.equals(loginButton)) {
            String username = usernameInput.text();
            String password = passwordInput.text();
            //校验数据...
            //做业务处理...
        } else if (component.equals(regButton)) {
            //获取usernameInput、passwordInput、repeatedPswdInput数据...
            //校验数据...
            //做业务处理...
        } else if (component.equals(selection)) {
            String selectedItem = selection.select();
            if (selectedItem.equals("login")) {
                usernameInput.show();
                passwordInput.show();
                repeatedPswdInput.hide();
                hintText.hide();
                //...省略其他代码
            } else if (selectedItem.equals("register")) {
                //....
            }
        }
    }
}

public class UIControl {
    private static final String LOGIN_BTN_ID = "login_btn";
    private static final String REG_BTN_ID = "reg_btn";
    private static final String USERNAME_INPUT_ID = "username_input";
    private static final String PASSWORD_INPUT_ID = "pswd_input";
    private static final String REPEATED_PASSWORD_INPUT_ID = "repeated_pswd_input";
    private static final String HINT_TEXT_ID = "hint_text";
    private static final String SELECTION_ID = "selection";

    public static void main(String[] args) {
        Button loginButton = (Button)findViewById(LOGIN_BTN_ID);
        Button regButton = (Button)findViewById(REG_BTN_ID);
        Input usernameInput = (Input)findViewById(USERNAME_INPUT_ID);
        Input passwordInput = (Input)findViewById(PASSWORD_INPUT_ID);
        Input repeatedPswdInput = (Input)findViewById(REPEATED_PASSWORD_INPUT_ID);
        Text hintText = (Text)findViewById(HINT_TEXT_ID);
        Selection selection = (Selection)findViewById(SELECTION_ID);

        Mediator dialog = new LandingPageDialog();
        dialog.setLoginButton(loginButton);
        dialog.setRegButton(regButton);
        dialog.setUsernameInput(usernameInput);
        dialog.setPasswordInput(passwordInput);
        dialog.setRepeatedPswdInput(repeatedPswdInput);
        dialog.setHintText(hintText);
        dialog.setSelection(selection);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.handleEvent(loginButton, "click");
            }
        });

        regButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.handleEvent(regButton, "click");
            }
        });

        //....
    }
}
```

