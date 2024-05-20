package com.example.desensitize.aop;import com.example.desensitize.annotation.Idempotent;import com.example.desensitize.dao.IdempotentRedisDAO;import com.example.desensitize.service.IdempotentKeyResolver;import lombok.SneakyThrows;import lombok.extern.slf4j.Slf4j;import org.aspectj.lang.JoinPoint;import org.aspectj.lang.annotation.Aspect;import org.aspectj.lang.annotation.Before;import org.springframework.context.ApplicationContext;import org.springframework.stereotype.Component;import org.springframework.util.Assert;import java.util.Map;/** * 拦截声明了 {@link Idempotent} 注解的方法，实现幂等操作 * * @author 芋道源码 */@Aspect@Component@Slf4jpublic class IdempotentAspect {    /**     * 获得 IdempotentKeyResolver     */    private final Map<String, IdempotentKeyResolver> keyResolverMap;    private final IdempotentRedisDAO idempotentRedisDAO;        private final ApplicationContext applicationContext;    public IdempotentAspect(Map<String, IdempotentKeyResolver>  keyResolverMap,                            IdempotentRedisDAO idempotentRedisDAO,                            ApplicationContext applicationContext) {        this.keyResolverMap = keyResolverMap;        this.idempotentRedisDAO = idempotentRedisDAO;        this.applicationContext = applicationContext;    }        public String getBeanName(Class<?> clazz) {        // 检查给定的类是否是Spring Bean        String[] beanNames = applicationContext.getBeanNamesForType(clazz);        if (beanNames.length > 0) {            // 如果存在多个Bean，则选择第一个Bean的名称            return beanNames[0];        } else {            // 如果不存在该类的Bean，则返回null            return null;        }    }        @SneakyThrows    @Before("@annotation(idempotent)")    public void beforePointCut(JoinPoint joinPoint, Idempotent idempotent) {        Class<? extends IdempotentKeyResolver> clazz = idempotent.keyResolver();        String beanName = this.getBeanName(clazz);        IdempotentKeyResolver keyResolver = keyResolverMap.get(beanName);        Assert.notNull(keyResolver, "找不到对应的 IdempotentKeyResolver");        // 解析 Key        String key = keyResolver.resolver(joinPoint, idempotent);        // 锁定 Key。        boolean success = idempotentRedisDAO.setIfAbsent(key, idempotent.timeout(), idempotent.timeUnit());        // 锁定失败，抛出异常        if (!success) {            log.info("[beforePointCut][方法({}) 参数({}) 存在重复请求]", joinPoint.getSignature().toString(), joinPoint.getArgs());            throw new Exception(idempotent.message());        }    }}