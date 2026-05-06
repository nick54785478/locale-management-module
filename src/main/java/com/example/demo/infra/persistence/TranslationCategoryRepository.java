package com.example.demo.infra.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;

@Repository
public interface TranslationCategoryRepository extends JpaRepository<TranslationCategory, String> {

	Optional<TranslationCategory> findByTypeAndCode(String type, String code);

	List<TranslationCategory> findByTypeIn(List<String> types);

	Page<TranslationCategory> findAll(Specification<TranslationCategory> specification, Pageable pageable);

	List<TranslationCategory> findByTypeInAndCodeIn(Collection<String> types, Collection<String> codes);
}
