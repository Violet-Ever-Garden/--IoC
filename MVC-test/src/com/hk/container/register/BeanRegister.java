package com.hk.container.register;

import bean.BeanDefinition;
import com.hk.container.context.ClassPathXmlApplicationContext;
import com.hk.container.reader.BeanDefinitionReader;
import com.hk.container.sterotype.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class BeanRegister {
    /**
     * ApplicationContext 特点，第一次加载即注入所有 bean 到容器
     */
    private BeanDefinitionReader reader;

    private ClassPathXmlApplicationContext classPathXmlApplicationContext;

    public BeanRegister(BeanDefinitionReader reader,ClassPathXmlApplicationContext classPathXmlApplicationContext) {
        this.reader = reader;
        this.classPathXmlApplicationContext = classPathXmlApplicationContext;
    }

    public void prepareBeanRegister() {
        for (String beanId : reader.getBeanDefinitionMap().keySet()) {
            BeanDefinition bd = this.reader.getBeanDefinition(beanId);
            // 单例模式，一个类对应一个 Bean，不是通过 id。常规单例模式是多次调用方法，只生成一个实例。此处是只会调用依次生成实例方法。
            Object bean = this.classPathXmlApplicationContext.getSingleton(beanId);
            if (bean == null) {
                bean = createBean(bd);
                this.registerSingleton(beanId, bean);
            }
        }
    }

    public void registerSingleton(String beanName, Object singletonObject) {
        Object oldObject = this.classPathXmlApplicationContext.getSingletonObjects().get(beanName);
        if (oldObject != null) {
            System.out.println("error," + oldObject + " had already registered");
        }
        this.classPathXmlApplicationContext.getSingletonObjects().put(beanName, singletonObject);
    }

    public Object createBean(BeanDefinition bd) {
        // 创建实例
        Object bean = instantiateBean(bd);
        // 填充属性（依赖注入）
        populateBean(bean);

        return bean;
    }


    private Object instantiateBean(BeanDefinition bd) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String beanClassName = bd.getBeanClassName();
        try {
            Class<?> clz = cl.loadClass(beanClassName);
            System.out.println(beanClassName);
            return clz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 利用反射，将字段与对象关联
     * 没有 setter 方法，利用 Field 的field.set()；有 setter 方法，利用 Method 的 Method.invoke()
     *
     * @param bean
     */
    private void populateBean(Object bean) {
        // 通过反射得到当前类所有的字段信息（Field 对象）。getFields() 获取公有字段
        Field[] fields = bean.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
//                Annotation ann = findAutowiredAnnotation(field);
                // 判断字段是否有 @Autowired 注解
                Annotation ann = field.getAnnotation(Autowired.class);
                // 根据是否有 Autowired 注解来决定是否注入
                if (ann != null) {
                    // 实际上，这里不是简单的通过 name 获取依赖，而是根据类型获取 getAutowiredBean(bean)
                    Object value = classPathXmlApplicationContext.getBean(field.getName());
                    if (value != null) {
                        // 设置字段可连接，相当于将非 public（private、default、package） 更改为 public
                        field.setAccessible(true) ;
                        // 通过反射设置字段的值
                        field.set(bean, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
