package bean;

public class BeanDefinition {
    private String id;

    private String beanClassName;

    public BeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    public String getBeanClassName() {

        return this.beanClassName;
    }

    public String getId() {
        return id;
    }
}
