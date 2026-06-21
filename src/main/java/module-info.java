module ch.hsr.ogv {
    requires java.prefs;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires jakarta.xml.bind;
    requires jakarta.activation;

    requires org.slf4j;
    requires org.apache.logging.log4j;

    requires fxyzlib;
    requires objmodelimporterjfx;

    opens ch.hsr.ogv.controller to javafx.fxml;

    opens ch.hsr.ogv.model to jakarta.xml.bind, org.glassfish.jaxb.core, org.glassfish.jaxb.runtime;
    opens ch.hsr.ogv.dataaccess to jakarta.xml.bind, org.glassfish.jaxb.core, org.glassfish.jaxb.runtime;

    exports ch.hsr.ogv;
}