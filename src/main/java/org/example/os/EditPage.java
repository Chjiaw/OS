package org.example.os;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EditPage extends Application {

    public TextArea ea;
    HelloController helloController;
    Stage stage = new Stage();
    Alert alert = new Alert(Alert.AlertType.NONE);
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloView.class.getResource("editPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        EditPage controller= fxmlLoader.getController();
        ea = controller.ea;
        stage.setScene(scene);
        stage.setTitle("File Editor");
        stage.show();
    }

    public void editpage(HelloController controller,FileOrdirectory file) throws SQLException, IOException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Statement sql1 = connection.createStatement();
        String SQL = "SELECT  * FROM directory_structure WHERE id = " + file.getId();
        ResultSet res = sql1.executeQuery(SQL);
        boolean editing = false;
        boolean opend = false;
        while (res.next()) {
            editing = res.getBoolean("editing");
            opend = res.getBoolean("opend");
        }
        if (editing) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("ERROR MessageBox");
            alert.setContentText("文件正在被编辑");
            alert.show();
        } else if(opend) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("ERROR MessageBox");
            alert.setContentText("文件已被打开，无法编辑");
            alert.show();
        }else{
            PreparedStatement pstmt = null;
            String SQL3 = null;
            SQL3 = "update directory_structure set editing = true where id = " + file.getId();
            pstmt = connection.prepareStatement(SQL3);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                helloController = controller;
                start(stage);
                ea.setText(file.getContent());
            }
        }
        Connection finalConnection = connection;
        stage.setOnCloseRequest(event->{
            System.out.println(ea.getText());
            PreparedStatement pstmt2 = null;
            String SQL3 = null;
            SQL3 = "update directory_structure set content = '" + ea.getText() + "',editing = false  where id = " + file.getId();
//            System.out.println(SQL3);
            try {
                pstmt2 = finalConnection.prepareStatement(SQL3);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int affectedRows = 0;
            try {
                affectedRows = pstmt2.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (affectedRows == 1) {
//                System.out.println(affectedRows);
                file.setContent(ea.getText());
                //file.setSize(ea.getLength());
                DBConnection.closeConnection(finalConnection);
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
