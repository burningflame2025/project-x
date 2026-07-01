module com.example.demo1 {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.xml;
    requires java.desktop;
    requires java.naming;
    requires java.sql;

    exports com.example.demo1;
    exports com.example.demo1.Controller;
    exports com.example.demo1.Model;
    exports com.example.demo1.View;

    opens com.example.demo1 to javafx.fxml;
    opens com.example.demo1.Controller to javafx.fxml;
    opens com.example.demo1.Model to javafx.fxml;
    opens com.example.demo1.View to javafx.fxml;
}