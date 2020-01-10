package top.niunaijun.livedata.core;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import top.niunaijun.livedata.annotations.ObserveLiveData;

/**
 * Created by sunwanquan on 2020/1/6.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@AutoService(Processor.class)
public class ObserveLiveDataProcessor extends AbstractProcessor {

    private Map<String, ObserveLiveDataProxy> mObserveLiveDataProxyMap;

    private Messager mMessager;
    private Elements mElementUtils; //元素相关的辅助类
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mObserveLiveDataProxyMap = new Hashtable<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(ObserveLiveData.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mObserveLiveDataProxyMap.clear();
        for (String s : processingEnv.getOptions().keySet()) {
            mMessager.printMessage(Diagnostic.Kind.WARNING, "key : " + s + ", value : " + processingEnv.getOptions().get(s));
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(ObserveLiveData.class)) {
            ObserveLiveData annotation = element.getAnnotation(ObserveLiveData.class);
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;

            String key;
            List<? extends TypeMirror> classValue = getClassValue(annotation);
            if (classValue != null && classValue.size() > 0) {
                key = classValue.get(0).toString();
            } else {
                // 如果是空，则调用自身内方法
                key = typeElement.getQualifiedName().toString();
            }
            ObserveInfo info = new ObserveInfo();
            info.setDataName(annotation.field());
            info.setMethod(executableElement);

            ObserveLiveDataProxy proxy = getProxy(typeElement, typeElement.asType().toString() + "$LiveData$" + key.replace(".", "-"));
            proxy.setTargetClass(key);
            proxy.getObserves().add(info);
        }

        for (ObserveLiveDataProxy value : mObserveLiveDataProxyMap.values()) {
            try {
                value.generateJavaCode().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static List<? extends TypeMirror> getClassValue(ObserveLiveData annotation) {
        try {
            annotation.target(); // this should throw
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null; // can this ever happen ??
    }

    public ObserveLiveDataProxy getProxy(TypeElement element, String fullName) {
        ObserveLiveDataProxy observeLiveDataProxy = mObserveLiveDataProxyMap.get(fullName);
        if (observeLiveDataProxy == null) {
            observeLiveDataProxy = new ObserveLiveDataProxy(element, mElementUtils, new HashSet<>());
            mObserveLiveDataProxyMap.put(fullName, observeLiveDataProxy);
        }
        return observeLiveDataProxy;
    }
}
