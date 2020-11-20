package com.hk.container.context;


import bean.BeanDefinition;
import com.hk.container.factory.BeanFactory;
import com.hk.container.reader.BeanDefinitionReader;
import com.hk.container.register.BeanRegister;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathXmlApplicationContext implements BeanFactory {

    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();

    private BeanDefinitionReader reader = new BeanDefinitionReader();

    private BeanRegister register;
    // 存放注解
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
            new LinkedHashSet<>();

    public ClassPathXmlApplicationContext(String configFile) {
//        this.autowiredAnnotationTypes.add(Autowired.class);
        reader.loadBeanDefinitions(configFile);
        register = new BeanRegister(reader,this);
        register.prepareBeanRegister();
    }





    @Override
    public Object getBean(String beanID) {
        BeanDefinition bd = this.reader.getBeanDefinition(beanID);
        // 单例模式，一个类对应一个 Bean，不是通过 id。常规单例模式是多次调用方法，只生成一个实例。此处是只会调用依次生成实例方法。
        Object bean = this.getSingleton(beanID);
        if (bean == null) {
            bean = register.createBean(bd);
            this.register.registerSingleton(beanID, bean);
        }
        return bean;
    }




    public Map<String,Object> getSingletonObjects(){
        return this.singletonObjects;
    }

    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }



    private Object getAutowiredBean(Object bean) throws ClassNotFoundException {
        Class<?> typeToMatch = bean.getClass();
        Object res = null;
        // 判断字段的类型是否跟 beanDefinitionMap 中 beanDefinition 的字段类型相同
        for (BeanDefinition bd : this.reader.getBeanDefinitionMap().values()) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> beanClass = cl.loadClass(bd.getBeanClassName());
            // 判断字段的类型是否跟依赖的类型是否匹配
            if (typeToMatch.isAssignableFrom(beanClass)) {
                res = getBean(bd.getId());
            }
        }
        return res;
    }

    /**
     * 查看 field 是否有注解
     *
     * @param ao
     * @return
     */
    private Annotation findAutowiredAnnotation(AccessibleObject ao) {
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            // type: stereotype.Autowired
            Annotation ann = getAnnotation(ao, type);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }

    /**
     * 查看 field 是否有 annotationType 类型的注解
     *
     * @param ae
     * @param annotationType
     * @param <T>
     * @return
     */
    private <T extends Annotation> T getAnnotation(AnnotatedElement ae, Class<T> annotationType) {
        T ann = ae.getAnnotation(annotationType);
        if (ann == null) {
            for (Annotation metaAnn : ae.getAnnotations()) {
                ann = metaAnn.annotationType().getAnnotation(annotationType);
                if (ann != null) {
                    break;
                }
            }
        }
        return ann;
    }

    /**
     * 设置字段可连接
     *
     * @param field
     */
    private void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
