package com.meteor.app.transaction;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaTransactionManager;
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
}
