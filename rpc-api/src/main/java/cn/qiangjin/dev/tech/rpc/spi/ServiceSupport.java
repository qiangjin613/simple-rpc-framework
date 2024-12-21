package cn.qiangjin.dev.tech.rpc.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI类加载器帮助类
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class ServiceSupport {

    public static final Map<String, Object> SINGLETON_SERVICES = new HashMap<>();

    public synchronized static <S> S load(Class<S> service) {
        return StreamSupport
                .stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
    }

    public synchronized static <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport
                .stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <S> S singletonFilter(S service) {
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = SINGLETON_SERVICES.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        }
        return service;
    }
}
