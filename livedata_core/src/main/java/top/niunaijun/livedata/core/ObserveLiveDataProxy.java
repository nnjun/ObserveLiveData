package top.niunaijun.livedata.core;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import top.niunaijun.livedata.core.utils.ClassUtils;


/**
 * Created by sunwanquan on 2020/1/8.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ObserveLiveDataProxy {

    private ClassName observer;
    private ClassName LifecycleOwner;
    private ClassName REGISTER = ClassName.get("top.niunaijun.livedata.api", "ObserveRegister");

    private static ClassName mTarget;
    private TypeElement mHostTypeElement;
    private Elements mElementUtils;
    private String mTargetClass;
    private String mRandClassName;
    private Set<ObserveInfo> mObserves = new HashSet<>();

    public ObserveLiveDataProxy(TypeElement targetTypeElement, Elements elementUtils, Set<ObserveInfo> observes, String lifecyclePackage) {
        mHostTypeElement = targetTypeElement;
        mElementUtils = elementUtils;
        mObserves = observes;

        if (lifecyclePackage == null || lifecyclePackage.length() == 0) {
            lifecyclePackage = "android.arch.lifecycle";
        }
        observer = ClassName.get(lifecyclePackage, "Observer");
        LifecycleOwner = ClassName.get(lifecyclePackage, "LifecycleOwner");
    }

    JavaFile generateJavaCode() {
        mTarget = ClassName.get(ClassUtils.getPackage(mTargetClass), ClassUtils.getName(mTargetClass));
        String packageName = mElementUtils.getPackageOf(mHostTypeElement).getQualifiedName().toString();

        MethodSpec.Builder registerMethod = MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(packageName, mHostTypeElement.getSimpleName().toString()), "host", Modifier.FINAL)
                .addParameter(mTarget, "target");

        for (ObserveInfo observeInfo : mObserves) {
            List<? extends VariableElement> parameters = observeInfo.getMethod().getParameters();
            if (parameters.size() > 1) {
                continue;
            }
            VariableElement variableElement = parameters.size() == 0 ? null : parameters.get(0);
            if (variableElement == null) {
                generaNotParamMethod(registerMethod, observeInfo);
            } else {
                generaParamMethod(registerMethod, observeInfo, variableElement);
            }
        }

        //generaClass
        TypeSpec observeClass = TypeSpec.classBuilder(mHostTypeElement.getSimpleName() + "$LiveData$" + mTargetClass.replace(".", "_"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(REGISTER,
                        ClassName.get(packageName, mHostTypeElement.getSimpleName().toString()),
                        mTarget
                ))
                .addMethod(registerMethod.build())
                .build();

        return JavaFile.builder(packageName, observeClass).build();
    }

    private void generaNotParamMethod(MethodSpec.Builder registerMethod, ObserveInfo observeInfo) {
        String statement = "$T $N_$N_observer = new $T() {\n" +
                "            @Override\n" +
                "            public void onChanged($T data) {\n" +
                "                host.$N();\n" +
                "            }\n" +
                "        };\n" +
                "        if (host instanceof $T) {\n" +
                "            target.$N.observe(($T) host, $N_$N_observer);\n" +
                "        } else {\n" +
                "            target.$N.observeForever($N_$N_observer);\n" +
                "        };";
        registerMethod.addStatement(statement,
                observer,
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName(),
                observer,

                Object.class,
                observeInfo.getMethod().getSimpleName(),

                LifecycleOwner,
                observeInfo.getDataName(),
                LifecycleOwner,
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName(),

                observeInfo.getDataName(),
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName()
        );
    }

    private void generaParamMethod(MethodSpec.Builder registerMethod, ObserveInfo observeInfo, VariableElement variableElement) {
        String statement = "$T<$T> $N_$N_observer = new $T<$T>() {\n" +
                "            @Override\n" +
                "            public void onChanged($T data) {\n" +
                "                host.$N(data);\n" +
                "            }\n" +
                "        };\n" +
                "        if (host instanceof $T) {\n" +
                "            target.$N.observe(($T) host, $N_$N_observer);\n" +
                "        } else {\n" +
                "            target.$N.observeForever($N_$N_observer);\n" +
                "        };";
        registerMethod.addStatement(statement,
                observer,
                ClassName.get(variableElement.asType()),
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName(),
                observer,
                ClassName.get(variableElement.asType()),

                ClassName.get(variableElement.asType()),
                observeInfo.getMethod().getSimpleName(),

                LifecycleOwner,
                observeInfo.getDataName(),
                LifecycleOwner,
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName(),

                observeInfo.getDataName(),
                observeInfo.getDataName(),
                observeInfo.getMethod().getSimpleName()
        );
    }

    public TypeElement getHostTypeElement() {
        return mHostTypeElement;
    }

    public void setHostTypeElement(TypeElement hostTypeElement) {
        mHostTypeElement = hostTypeElement;
    }

    public Elements getElementUtils() {
        return mElementUtils;
    }

    public void setElementUtils(Elements elementUtils) {
        mElementUtils = elementUtils;
    }

    public Set<ObserveInfo> getObserves() {
        return mObserves;
    }

    public void setObserves(Set<ObserveInfo> observes) {
        mObserves = observes;
    }

    public String getTargetClass() {
        return mTargetClass;
    }

    public void setTargetClass(String targetClass) {
        mTargetClass = targetClass;
    }

    public String getRandClassName() {
        return mRandClassName;
    }

    public void setRandClassName(String randClassName) {
        mRandClassName = randClassName;
    }
}
