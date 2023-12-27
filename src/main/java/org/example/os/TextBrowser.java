package org.example.os;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.*;

public class TextBrowser extends Application {
    private TextArea textArea = new TextArea();
    private TextArea textArea2 = new TextArea();
    Alert alert = new Alert(Alert.AlertType.NONE);
    HelloController helloController;
    Stage stage = new Stage();
    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
//        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea2.setEditable(true);
        textArea2.setWrapText(true);
        Scene scene = new Scene(borderPane, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
    public void openbrowser(HelloController controller, FileOrdirectory file) throws SQLException {
        // 查询文件是否正在被编辑，实现读写保护
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Statement sql1 = connection.createStatement();
        String SQL = "SELECT  * FROM directory_structure WHERE id = " + file.getId();
//        System.out.println(SQL);
        ResultSet res = sql1.executeQuery(SQL);
        System.out.println(res.getRow());
        boolean editing = true;
        while (res.next()) {
            editing = res.getBoolean("editing");
        }
        if (!editing) {
            PreparedStatement pstmt = null;
            String SQL3 = null;
            SQL3 = "update directory_structure set opend = opend + 1 where id = " + file.getId();
            pstmt = connection.prepareStatement(SQL3);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                helloController = controller;
                textArea.setText(file.getContent());
                stage.setTitle("File Browser                    " + file.getName());
                start(stage);
            }
            Connection finalConnection = connection;
            stage.setOnCloseRequest(event -> {
                PreparedStatement pstmt2 = null;
                String SQL2 = null;
                SQL2 = "update directory_structure set opend = opend - 1 where id = " + file.getId();
                try {
                    pstmt2 = finalConnection.prepareStatement(SQL2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                int affectedRows2 = 0;
                try {
                    affectedRows2 = pstmt2.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (affectedRows2 == 1) {
                    DBConnection.closeConnection(finalConnection);
                }
            });
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("ERROR MessageBox");
            alert.setContentText("文件正在被编辑");
            alert.show();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
