module org.example.os {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.jetbrains.annotations;

    opens org.example.os to javafx.fxml;
    exports org.example.os;
}