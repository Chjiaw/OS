package org.example.os;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class NewPage extends Application {
    Stage stage2 = new Stage();
    @FXML
    public ChoiceBox<Integer> ocb1;
    public ChoiceBox<Integer> ocb2;
    public ChoiceBox<Integer> ocb3;
    public ChoiceBox<Integer> gcb1;
    public ChoiceBox<Integer> gcb2;
    public ChoiceBox<Integer> gcb3;
    public ChoiceBox<Integer> otcb1;
    public ChoiceBox<Integer> otcb2;
    public ChoiceBox<Integer> otcb3;
    public TextField fname;
    public ChoiceBox<String> ftype;
    public Button submit;
    Stage stage = new Stage();

    Alert alert = new Alert(Alert.AlertType.NONE);
    HelloController hcontroller;
    User user = new User();
    FileOrdirectory file = new FileOrdirectory();
    Stack stack = new Stack<>();
    Deque<FileOrdirectory> deque = new LinkedList<>();

    @FXML


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloView.class.getResource("newPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        NewPage controller = fxmlLoader.getController();
//        controller.ftype.getItems().add("opeion 1");
        stage.setTitle("新建文件");
        stage.setScene(scene);
        fname = controller.fname;
        ftype = controller.ftype;
        ocb1 = controller.ocb1;
        ocb2 = controller.ocb2;
        ocb3 = controller.ocb3;
        gcb1 = controller.gcb1;
        gcb2 = controller.gcb2;
        gcb3 = controller.gcb3;
        otcb1 = controller.otcb1;
        otcb2 = controller.otcb2;
        otcb3 = controller.otcb3;
////        ftype.getItems().add()
        ftype.getItems().add("文档");
        ftype.getItems().add("文件夹");
        ocb1.getItems().add(1);
        ocb1.getItems().add(0);
        ocb2.getItems().add(1);
        ocb2.getItems().add(0);
        ocb3.getItems().add(1);
        ocb3.getItems().add(0);
        gcb1.getItems().add(1);
        gcb1.getItems().add(0);
        gcb2.getItems().add(1);
        gcb2.getItems().add(0);
        gcb3.getItems().add(1);
        gcb3.getItems().add(0);
        otcb1.getItems().add(1);
        otcb1.getItems().add(0);
        otcb2.getItems().add(1);
        otcb2.getItems().add(0);
        otcb3.getItems().add(1);
        otcb3.getItems().add(0);
        stage.show();
        submit = controller.submit;
        submit.setOnAction(event -> {
            try {
                newfile(user, file);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        ftype.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
//            System.out.println("Selected item: " + newValue);
            // 这里你可以执行进一步的逻辑处理，比如根据选择的项改变界面其他部分的内容或逻辑
        });
        stage.setOnHidden(event -> {
            stage2.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void newWindow(HelloController controller, User user,
                          Stage stage1, Deque<FileOrdirectory> deque
    ) throws Exception {
//        System.out.println("处于跟目录");
        this.user = user;
//        System.out.println("处于跟目录");
//        this.stack = stack;
        this.deque = deque;
//        if(!stack.empty()){
//            this.file = deque.getLast();
////            this.file = stack.peek();
//        }
        if(!deque.isEmpty()){
            this.file = deque.getLast();
        }


        hcontroller = controller;
        start(stage);
        stage2 = stage1;
    }

    private void newfile(User user, FileOrdirectory file) throws SQLException {
        Connection connection;
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 查询是否有重名文件
        // 应当查询父目录下是否有重名文件，而不是在整个目录结构中查询
        String SQL = null;
//        if(stack.empty()){
//            SQL = "select * from directory_structure where name = '" + fname.getText() + "' and parent_id is null";
//        }else{
//            SQL = "select * from directory_structure where name = '" + fname.getText() + "' and parent_id = " + file.getId();
//        }
        if(deque.isEmpty()){
            SQL = "select * from directory_structure where name = '" + fname.getText() + "' and parent_id is null";
        }else{
            SQL = "select * from directory_structure where name = '" + fname.getText() + "' and parent_id = " + file.getId();
        }
//        String SQL = null;

//        System.out.println(SQL);
        Statement sql = connection.createStatement();
        ResultSet res = sql.executeQuery(SQL);
        if (!res.next()) {
            // 执行插入操作
            PreparedStatement pstmt = null;
            String SQL3 = null;
//            if(!stack.empty()){
//                SQL3 = "insert into os.directory_structure(name,parent_id,is_directory,onwer,directory_structure.group_id," +
//                        "owner_permissions,group_permissions,other_permissions) value(?,?,?,?,?,?,?,?)";
//
//            }else{
//                SQL3 = "insert into os.directory_structure(name,is_directory,onwer,directory_structure.group_id," +
//                        "owner_permissions,group_permissions,other_permissions) value(?,?,?,?,?,?,?)";
//            }
            if(!deque.isEmpty()){
                SQL3 = "insert into os.directory_structure(name,parent_id,is_directory,onwer,directory_structure.group_id," +
                        "owner_permissions,group_permissions,other_permissions) value(?,?,?,?,?,?,?,?)";
            }else{
                SQL3 = "insert into os.directory_structure(name,is_directory,onwer,directory_structure.group_id," +
                        "owner_permissions,group_permissions,other_permissions) value(?,?,?,?,?,?,?)";
            }
            try {
                int i = 0;
                FileOrdirectory fil = new FileOrdirectory();
                pstmt = connection.prepareStatement(SQL3);
                i++;
                pstmt.setString(i, fname.getText());
//                if(!stack.empty()){
//                    i++;
//                    pstmt.setInt(i, file.getId());
//                }
                if(!deque.isEmpty()){
                    i++;
                    pstmt.setInt(i, file.getId());
                }
                i++;
                pstmt.setInt(i, ftype.getValue().equals("文档") ? 0 : 1);
                i++;
                pstmt.setInt(i, user.getId());
                i++;
                pstmt.setInt(i, user.getGroup());
//                System.out.println(user.getGroup());
                i++;
                pstmt.setInt(i, ocb1.getValue() * 4 + ocb2.getValue() * 2 + ocb3.getValue());
                i++;
                pstmt.setInt(i, gcb1.getValue() * 4 + gcb2.getValue() * 2 + gcb3.getValue());
                i++;
                pstmt.setInt(i, otcb1.getValue() * 4 + otcb2.getValue() * 2 + otcb3.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int affectedRows = 0;
            try {
                affectedRows = pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (affectedRows == 1) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setContentText("新建成功");
                alert.show();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setContentText("新建失败");
                alert.show();
            }
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setContentText("新建失败，已有重名文件");
            alert.show();
        }
    }
}
