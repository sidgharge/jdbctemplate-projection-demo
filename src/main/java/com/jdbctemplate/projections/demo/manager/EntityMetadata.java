package com.jdbctemplate.projections.demo.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityMetadata {
	
	private Class<?> clazz;

	private String tableName;
	
	List<String> dbFields = new ArrayList<>();
	
	private BiMap<String, String> entityDbFields = HashBiMap.create();
	
	private Map<String, Field> dbEntityFields = new LinkedHashMap<>();
}
