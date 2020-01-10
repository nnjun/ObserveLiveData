package top.niunaijun.livedata.core;

import javax.lang.model.element.ExecutableElement;

/**
 * Created by sunwanquan on 2020/1/8.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
class ObserveInfo {
    private String dataName;
    private ExecutableElement method;

    String getDataName() {
        return dataName;
    }

    void setDataName(String dataName) {
        this.dataName = dataName;
    }

    ExecutableElement getMethod() {
        return method;
    }

    void setMethod(ExecutableElement method) {
        this.method = method;
    }
}
