package com.example.demo.infra.spec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTranslationSpecification implements Specification<TranslationCategory> {


	private static final long serialVersionUID = 1L;

	private String type;
	
	private String code;

	@Override
	public Predicate toPredicate(Root<TranslationCategory> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(type)) {
			predicates.add(criteriaBuilder.equal(root.get("type"), type));
		}

		if (StringUtils.isNotBlank(code)) {
			predicates.add(criteriaBuilder.equal(root.get("code"), code));
		}

		Predicate[] predicateArray = new Predicate[predicates.size()];
		query.where(criteriaBuilder.and(predicates.toArray(predicateArray)));
		return query.getRestriction();
	}
}
