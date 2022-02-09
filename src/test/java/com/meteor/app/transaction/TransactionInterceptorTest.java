package com.meteor.app.transaction;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.meteor.app.entity.lock.PessimisticLockEntity;
import com.meteor.app.repo.PessimisticLockRepository;
import com.meteor.app.service.ItemService;
import com.meteor.app.service.LockPessimisticTestService;

import lombok.SneakyThrows;

@SpringBootTest
public class TransactionInterceptorTest {
    @Autowired
    private LockPessimisticTestService lockTestService;
    @Autowired
    private JpaTransactionManager jpaTransactionManager;
    @Autowired
    private PessimisticLockRepository pessimisticLockRepository;
    @Autowired
    private ItemService itemServiceForTest;//for test

    @Test
    void selectUpdateSumOrderTestTransactionInterceptor() throws Throwable {
//        jpaTransactionManager.
        PessimisticLockEntity pessimisticLockEntity = new PessimisticLockEntity();
        pessimisticLockEntity.setSum(1);

//        Method insertPessimisticLockEntity = lockTestService.getClass().getDeclaredMethod(
//                "insertPessimisticLockEntity", PessimisticLockEntity.class);
        Method insertPessimisticLockEntity = LockPessimisticTestService.class.getDeclaredMethod(
                "insertPessimisticLockEntity", PessimisticLockEntity.class);

        Constructor<LockPessimisticTestService> declaredConstructor =
                LockPessimisticTestService.class.getDeclaredConstructor(PessimisticLockRepository.class,
                                                                        ItemService.class);

        MethodInvocation methodInvocation = new MethodInvocation() {

            @Override
            public Object proceed() throws Throwable {
                return getMethod().invoke(getThis(), getArguments());
            }

            @SneakyThrows
            @Override
            public Object getThis() {
                return declaredConstructor.newInstance(pessimisticLockRepository, itemServiceForTest);
            }

            @Override
            public AccessibleObject getStaticPart() {
                return null;
            }

            @Override
            public Object[] getArguments() {
                return new Object[] { pessimisticLockEntity };
            }

            @Override
            public Method getMethod() {
                return insertPessimisticLockEntity;
            }
        };

        TransactionAttributeSource transactionAttributeSource = new TransactionAttributeSource() {

            @Override
            public boolean isCandidateClass(Class<?> targetClass) {
                return TransactionAttributeSource.super.isCandidateClass(targetClass);
            }

            @Override
            public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
                RuleBasedTransactionAttribute ruleBasedTransactionAttribute =
                        new RuleBasedTransactionAttribute();
                return ruleBasedTransactionAttribute;
            }
        };

        {

            TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
            transactionInterceptor.setTransactionManager(jpaTransactionManager);
            transactionInterceptor.invoke(methodInvocation);
//            lockTestService.insertPessimisticLockEntity(pessimisticLockEntity);
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(1);
        }
        {
            lockTestService.selectUpdateSumOrderTest(pessimisticLockEntity.getId());
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(30);
        }
    }

    @Test
    void transactionInterceptorRequiredTest() throws Throwable {
        PessimisticLockEntity pessimisticLockEntity = new PessimisticLockEntity();
        pessimisticLockEntity.setSum(1);
        String methodName = "pessimisticLockFunctionNoTran";

        Method method = Arrays.stream(LockPessimisticTestService.class.getDeclaredMethods()).filter(
                name -> name.getName().equals(methodName)
        ).findFirst().get();

        Object[] args = new Object[] {
                new Function<PessimisticLockRepository, Object>() {
                    @Override
                    public Object apply(PessimisticLockRepository t) {
                        t.save(pessimisticLockEntity);
                        return null;
                    }
                }
        };

        Constructor<LockPessimisticTestService> declaredConstructor =
                LockPessimisticTestService.class.getDeclaredConstructor(PessimisticLockRepository.class,
                                                                        ItemService.class);

        MethodInvocation methodInvocation = new MethodInvocation() {

            @Override
            public Object proceed() throws Throwable {
                return getMethod().invoke(getThis(), getArguments());
            }

            @SneakyThrows
            @Override
            public Object getThis() {
                return declaredConstructor.newInstance(pessimisticLockRepository, itemServiceForTest);
            }

            @Override
            public AccessibleObject getStaticPart() {
                return null;
            }

            @Override
            public Object[] getArguments() {
                return args;
            }

            @Override
            public Method getMethod() {
                return method;
            }
        };

        TransactionAttributeSource transactionAttributeSource = new TransactionAttributeSource() {

            @Override
            public boolean isCandidateClass(Class<?> targetClass) {
                return TransactionAttributeSource.super.isCandidateClass(targetClass);
            }

            @Override
            public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
                RuleBasedTransactionAttribute ruleBasedTransactionAttribute =
                        new RuleBasedTransactionAttribute();
                return ruleBasedTransactionAttribute;
            }
        };

        {

            TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
            transactionInterceptor.setTransactionManager(jpaTransactionManager);
            transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
            transactionInterceptor.invoke(methodInvocation);
//            lockTestService.pessimisticLockFunction();
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(1);
        }
        {
            lockTestService.selectUpdateSumOrderTest(pessimisticLockEntity.getId());
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(30);
        }
    }
}
