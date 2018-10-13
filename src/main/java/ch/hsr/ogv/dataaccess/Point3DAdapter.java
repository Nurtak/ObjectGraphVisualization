package ch.hsr.ogv.dataaccess;

import javafx.geometry.Point3D;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Point3DAdapter extends XmlAdapter<OGVPoint3D, Point3D> {

    /*
     * Java => XML
     */
    @Override
    public OGVPoint3D marshal(Point3D val) throws Exception {
        return new OGVPoint3D(val.getX(), val.getY(), val.getZ());
    }

    /*
     * XML => Java
     */
    @Override
    public Point3D unmarshal(OGVPoint3D val) throws Exception {
        return new Point3D(val.getX(), val.getY(), val.getZ());
    }

}
