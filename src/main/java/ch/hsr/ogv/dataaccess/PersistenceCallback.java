package ch.hsr.ogv.dataaccess;

@FunctionalInterface
public interface PersistenceCallback {
    void completed(boolean success);
}
