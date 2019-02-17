package com.jdbctemplate.projections.demo.manager;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.reflections.Reflections;

public class EntityManager {

	private String packageName;
	
	private Map<Class<?>, EntityMetadata> entityData;

	public Map<Class<?>, EntityMetadata> getEntityData() {
		return entityData;
	}

	public EntityManager(String packageName) {
		this.packageName = packageName;
		scan();
	}

	private void scan() {
		Reflections reflections = new Reflections(packageName);

		entityData = reflections.getTypesAnnotatedWith(Entity.class)
			.stream()
			.map(this::getMetadata)
			.collect(Collectors.toMap(EntityMetadata::getClazz, e -> e));
	}
	
	private EntityMetadata getMetadata(Class<?> entityClazz) {
		EntityMetadata metadata = new EntityMetadata();
		metadata.setClazz(entityClazz);
		metadata.setTableName(getTableName(entityClazz));

		Field[] fields = entityClazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			
			String dbFieldName = getDbFieldName(fields[i]);
			
			metadata.getEntityDbFields().put(fields[i].getName(), dbFieldName);
			metadata.getDbEntityFields().put(dbFieldName, fields[i]);
			
			metadata.getDbFields().add(dbFieldName);
		}
		return metadata;
	}

	private String getTableName(Class<?> entityClazz) {
		Table table = entityClazz.getAnnotation(Table.class);
		if (table == null) {
			return toCamelCase(entityClazz.getSimpleName());
		}
		return table.name();
	}

	private String getDbFieldName(Field field) {
		Column column = field.getAnnotation(Column.class);
		if (column == null) {
			return toCamelCase(field.getName());
		}
		return column.name();
	}

	private String toCamelCase(String string) {
		char c[] = string.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		return new String(c);
	}
}
