module com.example.coursework {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires mysql.connector.j;
    requires java.sql;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.coursework to javafx.fxml;
    exports com.example.coursework;

    opens com.example.coursework.fxControllers to javafx.fxml;
    exports com.example.coursework.fxControllers;

    opens com.example.coursework.model to javafx.fxml,org.hibernate.orm.core, jakarta.persistence;
    exports com.example.coursework.model;

    opens com.example.coursework.model.enums to javafx.fxml;
    exports com.example.coursework.model.enums;

}