package nau.william.capstonechat.services;

public interface ResultListener<T> {
    void onSuccess(T data);

    void onFailure(Exception e);
}
