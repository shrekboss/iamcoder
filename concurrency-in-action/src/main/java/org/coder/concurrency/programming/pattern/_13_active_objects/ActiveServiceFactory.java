package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ActiveServiceFactory {
    private final static ActiveMessageQueue queue = new ActiveMessageQueue();

    public static <T> T active(T instance) {
        Object proxy = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(), new ActiveInvocationHandler<>(instance));
        return (T) proxy;
    }

    private static class ActiveInvocationHandler<T> implements InvocationHandler {
        private final T instance;

        ActiveInvocationHandler(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if (method.isAnnotationPresent(ActiveMethod.class)) {
                //check the method
                this.checkMethod(method);
                ActiveMessage.Builder builder = new ActiveMessage.Builder();
                builder.useMethod(method).withObjects(args).forService(instance);
                Object result = null;
                if (this.isReturnFutureType(method)) {
                    result = new ActiveFuture<>();
                    builder.returnFuture((ActiveFuture) result);
                }
                queue.offer(builder.build());
                return result;
            } else {
                return method.invoke(instance, args);
            }
        }

        private void checkMethod(Method method) throws IllegalActiveMethod {
            if (!isReturnVoidType(method) && !isReturnFutureType(method)) {
                throw new IllegalActiveMethod("the method [" + method.getName()
                        + " return type must be void/Future");
            }
        }

        private boolean isReturnFutureType(Method method) {
            return method.getReturnType().isAssignableFrom(Future.class);
        }

        private boolean isReturnVoidType(Method method) {
            return method.getReturnType().equals(Void.TYPE);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OrderService orderService = active(new OrderServiceImpl());
        System.out.println(orderService);
        System.out.println(orderService.getClass());
        Future<String> future = orderService.findOrderDetails(23423);
        System.out.println("i will be returned immediately");
        System.out.println(future.get());
    }
}
