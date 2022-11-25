package com.njkim.multidatabase.aspect;

import com.njkim.multidatabase.annotation.SelectDB;
import com.njkim.multidatabase.model.TenantContextHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class DatabaseSelectorTest {
    @Test
    void select_annotation_to_type_class() {
        // GIVEN
        SelectorTypeTestClass target = new SelectorTypeTestClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        DatabaseSelector aspect = new DatabaseSelector();
        factory.addAspect(aspect);
        SelectorTypeTestClass proxy = factory.getProxy();

        // WHEN
        String currentTenantId = proxy.getCurrentTenantId();

        // THEN
        Assertions.assertThat(currentTenantId
                .equalsIgnoreCase("test"));
    }

    @Test
    void select_annotation_to_type_interface() {
        // GIVEN
        SelectorTypeTestInterface target = new SelectorTypeTestInterfaceImpl();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        DatabaseSelector aspect = new DatabaseSelector();
        factory.addAspect(aspect);
        SelectorTypeTestInterface proxy = factory.getProxy();

        // WHEN
        String currentTenantId = proxy.getCurrentTenantId();

        // THEN
        Assertions.assertThat(currentTenantId
                .equalsIgnoreCase("test"));
    }

    @Test
    void select_annotation_to_method() {
        // GIVEN
        SelectorMethodTestClass target = new SelectorMethodTestClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        DatabaseSelector aspect = new DatabaseSelector();
        factory.addAspect(aspect);
        SelectorMethodTestClass proxy = factory.getProxy();

        // WHEN
        String currentTenantId = proxy.getCurrentTenantId();

        // THEN
        Assertions.assertThat(currentTenantId
                .equalsIgnoreCase("test"));
    }
}

@SelectDB("test")
interface SelectorTypeTestInterface extends JpaRepository<String, String> {

    @SelectDB("test")
    String getCurrentTenantId();
}

class SelectorTypeTestInterfaceImpl implements SelectorTypeTestInterface {

    @Override
    public String getCurrentTenantId() {
        return TenantContextHolder.getCurrentTenantId();
    }

    @Override
    public List<String> findAll() {
        return null;
    }

    @Override
    public List<String> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<String> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<String> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends String> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends String> S save(S s) {
        return null;
    }

    @Override
    public <S extends String> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<String> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends String> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public <S extends String> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<String> iterable) {

    }

    @Override
    public void deleteAllInBatch(Iterable<String> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public String getOne(String s) {
        return null;
    }

    @Override
    public String getById(String s) {
        return null;
    }

    @Override
    public String getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends String> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends String> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends String> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends String> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends String> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends String> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends String, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}

@SelectDB("test")
class SelectorTypeTestClass {
    public String getCurrentTenantId() {
        return TenantContextHolder.getCurrentTenantId();
    }
}

class SelectorMethodTestClass {
    @SelectDB("test")
    public String getCurrentTenantId() {
        return TenantContextHolder.getCurrentTenantId();
    }
}

