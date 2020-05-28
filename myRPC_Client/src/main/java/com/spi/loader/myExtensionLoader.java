package com.spi.loader;

import com.spi.annotation.mySPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 13:51 2020/1/1
 * @Modified By:
 */
public class myExtensionLoader<T> {
    // SPI文件路径所在，dubbo中定义了三个，这边暂且定义一个
    private static final String SERVICES_DIRECTORY = "META-INF/myspi/";
    // 匹配默认扩展点的时候分割逗号使用，也是防止设置了多个value，因为默认只允许有一个
    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");
    // 扩展点加载器缓存
    private static final ConcurrentMap<Class<?>, myExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, myExtensionLoader<?>>();
    // 扩展点缓存
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

    // 接口class
    private final Class<?> type;
    // 默认扩展点的key，比如这边的  defaultmethod
    private String cachedDefaultName;

    // 缓存，用于缓存每一个key对应的class
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String, Class<?>>>();
    // 缓存，用于缓存每一个key对应的Holder
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();

    public myExtensionLoader(Class<?> type) {
        this.type = type;
    }

    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(mySPI.class);
    }

    // 根据接口名type获得myExtensionLoader类，相当于初始化
    public static <T> myExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null)
            throw new IllegalArgumentException("Extension type == null");
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because WITHOUT @" + mySPI.class.getSimpleName() + " Annotation!");
        }

        // 从EXTENSION_LOADERS中获取扩展，每一个扩展最多只有一个myExtensionLoader，比如说这边的Method，也只对应着一个myExtensionLoader
        myExtensionLoader<T> loader = (myExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            // 如果loader为空，则重新创建一个，相当于初始化
            EXTENSION_LOADERS.putIfAbsent(type, new myExtensionLoader<T>(type));
            loader = (myExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    //获取拓展点class,并缓存
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    //1.设置接口默认的实现类名  2.加载文件
    private Map<String, Class<?>> loadExtensionClasses() {
        final mySPI defaultAnnotation = type.getAnnotation(mySPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if (value != null && (value = value.trim()).length() > 0) {
                String[] names = NAME_SEPARATOR.split(value);
                if (names.length > 1) {
                    throw new IllegalStateException("more than 1 default extension name on extension " + type.getName()
                            + ": " + Arrays.toString(names));
                }
                if (names.length == 1) cachedDefaultName = names[0];
            }
        }
        Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
        loadFile(extensionClasses, SERVICES_DIRECTORY);
        return extensionClasses;
    }

    //获取类加载器
    private static ClassLoader findClassLoader() {
        return myExtensionLoader.class.getClassLoader();
    }

    //加载解析spi配置文件,然后加入缓存
    public void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName = dir + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                        try {
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                final int ci = line.indexOf('#');
                                if (ci >= 0) line = line.substring(0, ci);
                                line = line.trim();
                                if (line.length() > 0) {
                                    try {
                                        String name = null;
                                        int i = line.indexOf('=');
                                        if (i > 0) {
                                            name = line.substring(0, i).trim();
                                            line = line.substring(i + 1).trim();
                                        }
                                        if (line.length() > 0) {
                                            Class<?> clazz = Class.forName(line, true, classLoader);
                                            if (!type.isAssignableFrom(clazz)) {
                                                throw new IllegalStateException("Error when load extension class(interface: " +
                                                        type + ", class line: " + clazz.getName() + "), class "
                                                        + clazz.getName() + "is not subtype of interface.");
                                            }
                                            extensionClasses.put(name, clazz);
                                        }
                                    } catch (Throwable t) {
                                        IllegalStateException e = new IllegalStateException("Failed to load extension class(interface: " + type + ", class line: " + line + ") in " + url + ", cause: " + t.getMessage(), t);
//                                        exceptions.put(line, e);
                                    }
                                }
                            } // end of while read lines
                        } finally {
                            reader.close();
                        }
                    } catch (Throwable t) {
                        //logger.error("Exception when load extension class(interface: " +
                        //        type + ", class file: " + url + ") in " + url, t);
                    }
                } // end of while urls
            }
        } catch (Throwable e) {
            //logger.error("Exception when load extension class(interface: " + type + ", description file: " + fileName + ").", e);
        }
    }

    //根据获取到的拓展点class实例化成对象返回
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
//            throw findException(name);
        }
        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, (T) clazz.newInstance());//反射生成对象
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                    type + ")  could not be instantiated: " + t.getMessage(), t);
        }
    }

    // 根据name获得扩展点的class并返回
    public T getExtension(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Extension name == null");
        if ("true".equals(name)) {
            return getDefaultExtension();
        }
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<Object>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    // 获得默认扩展点
    public T getDefaultExtension() {
        getExtensionClasses();
        if (null == cachedDefaultName || cachedDefaultName.length() == 0
                || "true".equals(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }


}
