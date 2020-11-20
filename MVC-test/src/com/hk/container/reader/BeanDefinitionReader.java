package com.hk.container.reader;

import bean.BeanDefinition;
import com.hk.container.annotation.AnnotationMetadata;
import com.hk.container.sterotype.Component;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.asm.ClassReader;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDefinitionReader {
    private Map<String, BeanDefinition> beanDefinitionMap =new ConcurrentHashMap<>();

    public void loadBeanDefinitions(String configFile) {
        InputStream is = null;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            is = cl.getResourceAsStream(configFile); // 根据 configFile 获取 petstore-v1.xml 文件的字节流

            SAXReader reader = new SAXReader();
            Document doc = reader.read(is); // 将字节流转成文档格式

            Element root = doc.getRootElement(); // <beans>
            Iterator iter = root.elementIterator();
            while (iter.hasNext()) {
                Element ele = (Element) iter.next();
                parseComponentElement(ele);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据 component-scan 指定路径，找到路径下所有包含 @Component 注解的 Class 文件，作为 beanDefinition
     *
     * @param ele
     */
    private void parseComponentElement(Element ele) throws IOException {

        // 获取 component-scan 的路径
        String basePackagesStr = ele.attributeValue("base-package");
        String[] basePackages = basePackagesStr.split(",");
        for (String basePackage : basePackages) {

            File[] files = getFiles(basePackage);
            for (File file : files) {
                AnnotationMetadata annotationMetadata = getAnnotationMetadata(file);
                // 通过 hasAnnotation 判断是否有 @Component 注解
                if (annotationMetadata.hasAnnotation(Component.class.getName())) {
                    String beanId = (String) annotationMetadata.getAnnotationAttributes(Component.class.getName()).get("value");

                    String beanClassName = annotationMetadata.getClassName();
                    if (beanId == null) {
                        // 通过 class 路径获取类名，并将首字母小写
                        beanId = Introspector.decapitalize(ClassUtils.getShortName(beanClassName));
                    }

                    BeanDefinition bd = new BeanDefinition(beanId, beanClassName);
                    this.beanDefinitionMap.put(beanId, bd);
                }
            }
        }
    }



    /**
     * 利用字节码技术，将注解元数据存放在 AnnotationMetadata 中，一个 file 对应一个 AnnotationMetadata
     * <p>
     * 待优化：去除 AnnotationMetadata，直接获取注解
     *
     * @param file
     * @return
     * @throws IOException
     */
    public AnnotationMetadata getAnnotationMetadata(File file) throws IOException {

        // file 是路径，is 相当于字节码文件流
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        // 此时使用了 Spring 框架的 ClassReader，待优化为使用原生类
        ClassReader classReader;

        try {
            // 通过文件流设置 classReader
            classReader = new ClassReader(is);
        } finally {
            is.close();
        }

        AnnotationMetadata visitor = new AnnotationMetadata();
        // classReader 利用字节码技术，从文件流中获取元数据，设置到 AnnotationMetadata 中
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);

        return visitor;
    }

    /**
     * 获取指定路径下的所有 Class 文件
     *
     * @param basePackage
     * @return
     */
    private File[] getFiles(String basePackage) {
        String location = ClassUtils.convertClassNameToResourcePath(basePackage);
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        File rootDir = new File(url.getFile());
        Set<File> matchingFiles = new LinkedHashSet<>(8);
        doRetrieveMatchingFiles(rootDir, matchingFiles);
        return matchingFiles.toArray(new File[0]);
    }

    /**
     * 通过递归获取文件夹下的文件
     *
     * @param dir
     * @param result
     */
    private void doRetrieveMatchingFiles(File dir, Set<File> result) {

        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            return;
        }
        for (File content : dirContents) {
            if (content.isDirectory()) {
                if (!content.canRead()) {
                } else {
                    doRetrieveMatchingFiles(content, result);
                }
            } else {
                result.add(content);
            }
        }
    }

    public BeanDefinition getBeanDefinition(String beanID) {

        return this.beanDefinitionMap.get(beanID);
    }


    public Map<String, BeanDefinition> getBeanDefinitionMap(){
        return this.beanDefinitionMap;
    }
}
