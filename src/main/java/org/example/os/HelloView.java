package org.example.os;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class HelloView extends Application {

    private TableView<User> tableView = new TableView<>();
    private ObservableList<User> dataList = FXCollections.observableArrayList();
    HelloController controller;
    Alert alert = new Alert(AlertType.NONE);

    // 启动方法
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloView.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader1.load());
        controller = fxmlLoader1.getController();
        primaryStage.setTitle("Login to File System");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(String.valueOf(HelloView.class.getResource("登录.png"))));
        controller.Login_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String name = controller.username_text.getText();
                String password = controller.password_text.getText();
                Login login = new Login();
                int state;
                try {
                    state = login.login(name, password);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (state != 0) {
                    alert.setAlertType(AlertType.CONFIRMATION);
                    alert.setTitle("确认消息");
                    alert.setContentText("登录成功");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == ButtonType.OK) {
                            // 用户选择了"确认"
                            DisplayWindow displayWindow = new DisplayWindow();
                            try {
                                displayWindow.openwindow(controller, state);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            primaryStage.hide();
                        } else {
                            // 用户选择了"取消"
                            // 在这里执行“取消”按钮的逻辑
                            controller.username_text.setText("");
                            controller.password_text.setText("");
                        }
                    }
                } else {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("错误提示");
                    alert.setContentText("登录失败");
                    alert.show();
                    controller.username_text.setText("");
                    controller.password_text.setText("");
                }
            }
        });
        primaryStage.show();
    }

    // 主方法
    public static void main(String[] args) {
        launch(args);
    }
}
