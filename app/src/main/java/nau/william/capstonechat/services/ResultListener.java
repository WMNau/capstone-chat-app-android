package nau.william.capstonechat.services;

public interface ResultListener<String, T> {
    void onSuccess(String key, T data);

    void onChange(String key, T data);

    void onFailure(Exception e);
}
