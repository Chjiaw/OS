package org.example.os;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.os.HelloController;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;


public class DisplayWindow extends Application {


    public Label path;
    Stage stage = new Stage();
    HelloController hcontroller;
    User user = new User();
    @FXML
    public Label username;
    public Button refresh;
    public TreeView<String> tree;
    public TableView<FileOrdirectory> tableView;
    public TableColumn<FileOrdirectory, String> c1;
    public TableColumn<FileOrdirectory, Boolean> c0;
    public TableColumn<FileOrdirectory, Date> c2;
    public TableColumn<FileOrdirectory, Integer> c3;
    public Button upper;
    private ObservableList<FileOrdirectory> observableListData;
    private Stack<FileOrdirectory> stack = new Stack<>();
    Deque<FileOrdirectory> deque = new LinkedList<>();
    private FileOrdirectory upper_class = null;
    Alert alert = new Alert(AlertType.NONE);

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloView.class.getResource("displayWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        DisplayWindow controller = fxmlLoader.getController();
        upper = controller.upper;
        stage.setTitle("文件系统");
        username = controller.username;
        tableView = controller.tableView;
        tree = controller.tree;
        TreeItem<String> rootItem = new TreeItem<>("Root");
        buildTree(rootItem);
        tree.setRoot(rootItem);
        tree.setShowRoot(false); // 隐藏根节点
        tableView.setEditable(true);
        this.path = controller.path;
        controller.c1.setCellValueFactory(new PropertyValueFactory<>("name"));
        controller.c2.setCellValueFactory(new PropertyValueFactory<>("updated_at"));
        controller.c3.setCellValueFactory(new PropertyValueFactory<>("size"));
        controller.c0.setCellValueFactory(new PropertyValueFactory<>("is_directory"));
        controller.c1.setCellFactory(TextFieldTableCell.forTableColumn());
        controller.c1.setOnEditCommit(t ->
                (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setName(t.getNewValue()));
        controller.c0.setCellFactory(column -> new TableCell<FileOrdirectory, Boolean>() {
            @Override
            protected void updateItem(Boolean isDirectory, boolean empty) {
                super.updateItem(isDirectory, empty);
                if (empty || isDirectory == null) {
                    setGraphic(null);
                } else {
                    FontIcon icon = new FontIcon();
                    if (isDirectory) {
                        icon.setIconCode(BootstrapIcons.FOLDER);
                    } else {
                        icon.setIconCode(BootstrapIcons.FILE_EARMARK);
                    }
                    icon.setIconSize(16); // 设置图标大小
                    setGraphic(icon);
                }
            }
        });
        refresh = controller.refresh;
        refresh.setOnAction(event -> {
            TreeItem<String> root = new TreeItem<>();
            tree.setRoot(null);
            try {
                buildTree(root);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            tree.setRoot(root);
            tree.setShowRoot(false); // 隐藏根节点
        });
        observableListData = FXCollections.observableArrayList();
        tableView.setRowFactory(tv -> {
            TableRow<FileOrdirectory> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            // 创建菜单项
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                // 处理编辑操作
                FileOrdirectory item = row.getItem(); // 获取当前行的项目
                if(!item.isIs_directory()){
                    boolean permission = writepermission(item);
                    if (permission) {
                        EditPage editPage = new EditPage();
                        try {
                            editPage.editpage(hcontroller, item);
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        alert.setAlertType(AlertType.ERROR);
                        alert.setTitle("Error MessageBox");
                        alert.setContentText("你无权编辑此文件");
                        alert.show();
                    }
                }else{
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Error MessageBox");
                    alert.setContentText("目录项不可编辑");
                    alert.show();
                }

            });

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                FileOrdirectory item = row.getItem();

                boolean permission = writepermission(item);
                if (permission) {
                    Connection connection = null;
                    try {
                        connection = DBConnection.getConnection();
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    PreparedStatement pstmt = null;
                    String SQL3 = null;
                    SQL3 = "delete from os.directory_structure where id = " + item.getId();
                    try {
                        pstmt = connection.prepareStatement(SQL3);
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
                        tableView.getItems().remove(row.getItem());
                    }
                } else {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Error MessageBox");
                    alert.setContentText("你无权删除此文件");
                    alert.show();
                }

            });

            MenuItem openItem = new MenuItem("Open");
            openItem.setOnAction(actionEvent -> {
                FileOrdirectory item = row.getItem(); // 获取当前行的项目
                Openfile(item);
                // 设置Opend 为 True
                // 打开文件阅读界面
                // 关闭文件阅读界面
                // 设置Opend 为 False
            });
            //MenuItem renameItem = new MenuItem("rename");
            // 未实现重命名的权限检查
//            renameItem.setOnAction(event -> {
//                int selectedIdx = tableView.getSelectionModel().getSelectedIndex();
////                boolean permission = xpermission()
//                if (selectedIdx >= 0) {
//                    tableView.requestFocus();
//                    tableView.getSelectionModel().select(selectedIdx);
//                    tableView.edit(selectedIdx, controller.c1); // c1为你要编辑的列
//                    TableCell<?, ?> cell = controller.c1.getCellFactory().call(controller.c1);
//                    if (cell != null) {
//                        // 检查单元格是否处于可编辑状态，这取决于单元格的实现方式
//                        if (cell.isEditable() && cell instanceof TextFieldTableCell<?, ?>) {
//                            ((TextFieldTableCell<?, ?>) cell).startEdit();
//                            // 注：可能还需要其他的代码来显示编辑的TextField控件和确保它获得焦点
//                            // 例如，你可能需要调用 textField.requestFocus() 之类的
//                        }
//                    }
//                }
//            });
            // 将菜单项添加到上下文菜单
            contextMenu.getItems().addAll(editItem, deleteItem, openItem);//, renameItem);

            ContextMenu emptymenu = new ContextMenu();
            MenuItem new_folder = new MenuItem("new folder");
            new_folder.setOnAction(actionEvent -> {
//                System.out.println(deque.isEmpty());
                if (!deque.isEmpty()) {
                    // 获取父目录
//                    FileOrdirectory file = stack.peek();
                    FileOrdirectory file = deque.getLast();
                    // 检查用户对父目录是否有写权限
                    boolean permission = writepermission(file);
                    if (permission) {
                        NewPage npage = new NewPage();
                        try {
//                            npage.newWindow(hcontroller, user, stage, stack);
                            npage.newWindow(hcontroller, user, stage, deque);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    stage.hide();
                } else {

                    //处于根目录时
                    NewPage npage = new NewPage();
                    try {

//                        npage.newWindow(hcontroller, user, stage, stack);
                        npage.newWindow(hcontroller, user, stage, deque);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            emptymenu.getItems().addAll(new_folder);
            tableView.setContextMenu(emptymenu);
            // 只显示非空行的上下文菜单
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(javafx.beans.binding.Bindings.when(row.itemProperty().isNull())
                                    .then(emptymenu)   // 为空行设置特定菜单
                                    .otherwise(contextMenu))
            );

            return row;
        });
        tableView.setItems(observableListData);
        //双击列表项--处理逻辑
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!tableView.getSelectionModel().isEmpty())) {
                FileOrdirectory selectedFileOrDirectory = tableView.getSelectionModel().getSelectedItem();
                Openfile(selectedFileOrDirectory);
            }
        });
        //上层目录点击逻辑
        upper.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (deque.isEmpty()) {//stack.empty()
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Error MessageBox");
                    alert.setContentText("当前处于根目录");
                    alert.show();
                } else {
//                    upper_class = stack.pop();
                    upper_class = deque.removeLast();
                    Connection connection = null;
                    try {
                        connection = DBConnection.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Statement sql1 = null;
                    try {
                        sql1 = connection.createStatement();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    String SQL = null;
                    if (upper_class.getParent_id() == 0) {
                        SQL = "select * from directory_structure where parent_id is null";
                    } else {
                        SQL = "select * from directory_structure where parent_id =" + upper_class.getParent_id();
                    }
                    ResultSet res = null;
                    try {
                        res = sql1.executeQuery(SQL);
                        observableListData.clear();
                        while (res.next()) {
                            addItem(res);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    DBConnection.closeConnection(connection);
                }
                Refreshpath();
            }

        });//单击上一层--处理逻辑
        stage.setScene(scene);
        stage.show();
    }

    public void openwindow(HelloController controller, int id) throws Exception {
        hcontroller = controller;
        start(stage);
        loadDataFromDatabase(id);
    }

    private void loadDataFromDatabase(int id) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        Statement sql1 = connection.createStatement();
        String SQL = "select * from user where id =" + id;
        ResultSet res = sql1.executeQuery(SQL);
        while (res.next()) {
            user.setUsername(res.getString("username"));
            user.setId(res.getInt("id"));
            user.setGroup(res.getInt("group"));
            String s = user.getUsername();
            username.setText("用户：" + s);
        }
        Statement sql2 = connection.createStatement();
        String SQL2 = "select * from directory_structure where parent_id is null";
        ResultSet res2 = sql2.executeQuery(SQL2);

        int i = 0;
        while (res2.next()) {
            addItem(res2);
        }
        DBConnection.closeConnection(connection);
        Refreshpath();
    }

    private void addItem(ResultSet res) throws SQLException {
        FileOrdirectory file = new FileOrdirectory();
        file.setId(res.getInt("id"));
        file.setName(res.getString("name"));
        file.setParent_id(res.getInt("parent_id"));
        file.setIs_directory(res.getBoolean("is_directory"));
        file.setSize(res.getInt("size"));
        file.setCreated_at(res.getDate("created_at"));
        file.setUpdated_at(res.getDate(6));
        file.setOnwer(res.getInt("onwer"));
        file.setOpend(res.getBoolean("opend"));
        file.setEditing(res.getBoolean("editing"));
        file.setContent(res.getString("content"));
        file.setGroup_permissions(res.getInt("group_permissions"));
        file.setOwner_permissions(res.getInt("owner_permissions"));
        file.setOther_permissions(res.getInt("other_permissions"));
        observableListData.add(file);
    }

    private void Openfile(FileOrdirectory selectedFileOrDirectory) {
        if (selectedFileOrDirectory.isIs_directory()) {
            boolean permission = xpermission(selectedFileOrDirectory);
            if (permission) {
//                System.out.println("");
                boolean rpermission = readpermission(selectedFileOrDirectory);
//                stack.push(selectedFileOrDirectory);
                deque.addLast(selectedFileOrDirectory);
                if (!rpermission) {
                    observableListData.clear();
                } else {
                    Connection connection = null;
                    try {
                        connection = DBConnection.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Statement sql1 = null;
                    try {
                        sql1 = connection.createStatement();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    String SQL = "select * from directory_structure where parent_id =" + selectedFileOrDirectory.getId();
                    ResultSet res = null;
                    try {
                        res = sql1.executeQuery(SQL);
                        observableListData.clear();
                        while (res.next()) {
                            addItem(res);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    DBConnection.closeConnection(connection);
                }

            } else {
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle("Error MessageBox");
                alert.setContentText("你没有该目录的执行权限");
                alert.show();
            }

        } else {
            boolean permission = readpermission(selectedFileOrDirectory);
            if (permission) {
                TextBrowser textBrowser = new TextBrowser();
                try {
                    textBrowser.openbrowser(hcontroller, selectedFileOrDirectory);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle("Error MessageBox");
                alert.setContentText("你无权查看此文件");
                alert.show();
            }
        }
        Refreshpath();
    }

    private void buildTree(TreeItem<String> rootItem) throws SQLException, ClassNotFoundException {
        // 建立数据库连接
        Connection connection = DBConnection.getConnection(); // 使用DBConnection类获取数据库连接
        // 创建一个SQL语句执行对象
        Statement sql = connection.createStatement(); // 创建一个Statement对象，用于发送SQL语句到数据库
        // 定义要执行的SQL查询语句
        String SQL = "SELECT * FROM directory_structure"; // SQL查询，用于获取directory_structure表中的所有数据
        // 执行查询并获取结果集
        ResultSet rs = sql.executeQuery(SQL); // 执行SQL查询并返回结果集
        // 创建一个映射，用于存储目录结构中的每个节点及其对应的TreeItem对象
        Map<Integer, TreeItem<String>> treeMap = new HashMap<>();
        treeMap.put(0, rootItem); // 将根节点加入映射
        // 遍历结果集
        while (rs.next()) {
            // 从结果集中提取数据
            int id = rs.getInt("id"); // 获取记录的id
            String name = rs.getString("name"); // 获取记录的name
            int parentId = rs.getInt("parent_id"); // 获取记录的parent_id
            boolean isDirectory = rs.getBoolean("is_directory"); // 获取记录是否表示一个目录
            // 根据提取的数据创建一个新的TreeItem
            TreeItem<String> item = new TreeItem<>(name); // 创建新的TreeItem节点
            // 设置节点图标，根据它是否代表一个目录
            if (isDirectory) {
                item.setGraphic(new FontIcon(BootstrapIcons.FOLDER)); // 如果是目录，设置为文件夹图标
            } else {
                item.setGraphic(new FontIcon(BootstrapIcons.FILE)); // 如果是文件，设置为文件图标
            }
            // 将新节点加入映射
            treeMap.put(id, item); // 将新节点与其id关联在映射中
            // 将新节点添加到其父节点
            TreeItem<String> parentItem = treeMap.getOrDefault(parentId, rootItem); // 获取父节点
            parentItem.getChildren().add(item); // 将新节点作为子节点添加到父节点
        }
    }


    private boolean readpermission(FileOrdirectory file) {
        int a = 0, b = 0, c = 0;
        if (user.getId() == file.getOnwer()) {//拥有者
            //检查是否有权限
            a = file.getOwner_permissions() & 4;
        } else if (user.getGroup() == file.getGroup_id()) {//同组用户
            //检查是否有权限
            b = file.getGroup_permissions() & 4;
        } else {//其他用户
            //检查是否有权限
            c = file.getOther_permissions() & 4;
        }
        if (a != 0 || b != 0 || c != 0) {
            return true;

        } else {
            return false;
        }
    }

    private boolean writepermission(@org.jetbrains.annotations.NotNull FileOrdirectory file) {
        int a = 0, b = 0, c = 0;
        if (user.getId() == file.getOnwer()) {//拥有者
            a = file.getOwner_permissions() & 2;
            System.out.println(a);
        } else if (user.getGroup() == file.getGroup_id()) {//是同组用户
            b = file.getGroup_permissions() & 2;
        } else {//是其他用户
            c = file.getOther_permissions() & 2;
        }
        if (a != 0 || b != 0 || c != 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean xpermission(FileOrdirectory file) {
        int a = 0, b = 0, c = 0;
        if (user.getId() == file.getOnwer()) {//拥有者
            a = file.getOwner_permissions() & 1;
            System.out.println(a);
        } else if (user.getGroup() == file.getGroup_id()) {//是同组用户
            b = file.getGroup_permissions() & 1;
        } else {//是其他用户
            c = file.getOther_permissions() & 1;
        }
        if (a != 0 || b != 0 || c != 0) {
            return true;
        } else {
            return false;
        }
    }
    private void Refreshpath(){
        path.setText("根目录/");
        for(FileOrdirectory element : deque) {
            path.setText(path.getText()  +element.getName()+ "/") ;
        }
    }
}
