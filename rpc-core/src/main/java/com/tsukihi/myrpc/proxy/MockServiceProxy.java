package com.tsukihi.myrpc.proxy;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK动态代理）
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @param proxy
     * @param method
     * @param args
     * @return Object
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据方法返回值类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象（自定义）
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type){
        // 基本类型
        if(type.isPrimitive()){
            if(type == boolean.class){
                return false;
            }else if(type == short.class){
                return (short) 6657;
            }else if(type == int.class) {
                return 0;
            }else if(type == long.class) {
                return 0L;
            }else if(type == float.class) {
                return 0.0f;
            }else if(type == double.class) {
                return 0.0d;
            }else if(type == char.class) {
                return ' ';
            }else if(type == byte.class) {
                return (byte) 0;
            }
        }
        // 对象类型

        // todo 使用Faker之类的伪造数据生成库
        /**
         * 首先，定义一个MockDataGenerator类，该类包含一个静态的Faker实例和一个processedClasses集合。
         * Faker实例用于生成模拟数据，processedClasses集合用于跟踪已经处理过的类，防止无限递归。
         * generateMockData方法是主要的工作方法，它接受一个类的Class对象作为参数。
         * 这个方法首先检查processedClasses集合是否已经包含了这个类，如果包含则返回null以防止无限递归。
         * 然后，这个方法创建一个新的类实例，并遍历类的所有字段。
         * 对于每个字段，如果字段的类型是String，则使用Faker生成一个句子并设置为字段的值；
         * 如果字段的类型是int，则使用Faker生成一个随机数字并设置为字段的值；……
         * 否则，递归调用generateMockData方法生成一个新的模拟对象并设置为字段的值。
         * 在处理完所有字段后，这个方法从processedClasses集合中移除当前类，并返回创建的类实例。
         * 如果在创建类实例或设置字段值时发生异常，这个方法会捕获这个异常并将其包装为RuntimeException抛出。
         *
         * 问题：
         * 无限递归：如果类的字段引用了自身或者存在循环引用，那么这个方法可能会陷入无限递归。
         * 虽然这个方法使用了一个processedClasses集合来防止无限递归，
         * 但是这个集合在每次调用generateMockData方法时都会被清空，所以它不能防止在嵌套结构中的无限递归。
         * 并发问题：如果这个方法在多线程环境中被调用，那么processedClasses集合可能会遇到并发问题。
         * 你可能需要使用一个线程安全的集合，如ConcurrentHashMap的keySet视图，来替代HashSet。
         */

        Faker faker = new Faker();
        // Integer
        if(type == Integer.class){
            return faker.number().randomNumber();
        }
        // String
        if(type == String.class){
            return faker.name().fullName();
        }

        // 其他类型
        return null;
    }

}
