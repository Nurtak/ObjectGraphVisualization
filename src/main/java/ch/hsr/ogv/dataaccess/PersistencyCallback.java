package ch.hsr.ogv.dataaccess;

@FunctionalInterface
public interface PersistencyCallback {
    void completed(boolean success);
}
