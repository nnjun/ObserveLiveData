package top.niunaijun.livedata.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.niunaijun.livedata.annotations.ObserveLiveData;

/**
 * ObserveLiveData register Manager
 */
public class ObserveManager {
    private static Map<String, List<Class<?>>> sClassMap = new Hashtable<>();
    private static Map<String, List<ClassMapping>> sClassAllMap = new Hashtable<>();
    private static Map<String, Class> sClassCacheMap = new Hashtable<>();

    /**
     * register LiveData
     *
     * @param host    instance of onConfig method
     * @param targets instance of App.class
     * @<code> @ObserveLiveData(target = App.class, field = " mConfigLiveData ")
     * @<code> public void onConfig(Config config){}
     */
    public static void register(Object host, Object... targets) {
        for (Object object : targets) {
            register0(allMapping(host.getClass()), host, object);
        }
    }

    private static List<ClassMapping> allMapping(Class<?> host) {
        String hostClassName = host.getName();
        List<ClassMapping> mapping = sClassAllMap.get(hostClassName);
        if (mapping != null) {
            return mapping;
        }
        mapping = new ArrayList<>();
        List<String> className = new ArrayList<>();
        className.add(host.getName());
        Class<?> superclass = host;
        while (true) {
            superclass = superclass.getSuperclass();
            if (superclass != null) {
                className.add(superclass.getName());
                sClassCacheMap.put(superclass.getName(), superclass);
            } else {
                break;
            }
        }
        Set<String> tmp = new HashSet<>();
        for (String name : className) {
            Class<?> clazz = getClass(name);
            if (clazz == null) {
                continue;
            }
            List<Class<?>> classes = classMapping(clazz);
            for (Class<?> aClass : classes) {
                String mappingName = getMappingName(clazz, aClass);
                if (!tmp.contains(mappingName)) {
                    tmp.add(mappingName);
                    mapping.add(new ClassMapping(clazz, aClass));
                }
            }
        }
        sClassAllMap.put(hostClassName, mapping);
        return mapping;
    }

    private static List<Class<?>> classMapping(Class<?> clazz) {
        String name = clazz.getName();
        List<Class<?>> mapping = sClassMap.get(name);
        if (mapping != null) {
            return mapping;
        }
        mapping = new ArrayList<>();
        // @ObserveLiveData in the public method
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ObserveLiveData.class)) {
                ObserveLiveData annotation = method.getAnnotation(ObserveLiveData.class);
                if (annotation.target().length > 0) {
                    mapping.add(annotation.target()[0]);
                } else {
                    mapping.add(clazz);
                }
            }
        }
        sClassMap.put(name, mapping);
        return mapping;
    }

    private static void register0(List<ClassMapping> mapping, Object host, Object target) {
        for (ClassMapping classMapping : mapping) {
            Class<?> hostClass = classMapping.getHost();
            Class<?> targetClass = classMapping.getTarget();
            try {
                if ((hostClass.isAssignableFrom(host.getClass()) && targetClass.isAssignableFrom(target.getClass()))
                        || (target.getClass().getName().equals(targetClass.getName()))) {
                    Class<?> proxy = getClass(getMappingName(hostClass, targetClass));
                    if (proxy == null) {
                        continue;
                    }
                    ObserveRegister o = (ObserveRegister) proxy.newInstance();
                    o.register(host, target);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Class<?> getClass(String classFullName) {
        try {
            Class<?> proxy = sClassCacheMap.get(classFullName);
            if (proxy == null) {
                proxy = Class.forName(classFullName);
                sClassCacheMap.put(classFullName, proxy);
            }
            return proxy;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getMappingName(Class<?> host, Class<?> target) {
        return host.getName() + "$LiveData$" + target.getName().replace(".", "_");
    }
}
