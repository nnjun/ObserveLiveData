package top.niunaijun.livedata.api;

/**
 * Created by sunwanquan on 2020/1/10.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ClassMapping {
    private Class<?> host;
    private Class<?> target;

    ClassMapping(Class<?> host, Class<?> target) {
        this.host = host;
        this.target = target;
    }

    public Class<?> getHost() {
        return host;
    }

    public void setHost(Class<?> host) {
        this.host = host;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }
}
