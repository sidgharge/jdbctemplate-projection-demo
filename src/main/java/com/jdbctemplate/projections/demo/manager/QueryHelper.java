package com.jdbctemplate.projections.demo.manager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.google.common.collect.BiMap;

@Repository
public class QueryHelper {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private EntityManager entityManager;

	public <T> Optional<T> findBy(String whereClause, Object[] conditionValues, List<String> entityFields, Class<T> clazz) {
		String table = entityManager.getEntityData().get(clazz).getTableName();
		String query = "SELECT " + getDbFields(entityFields, clazz) + " FROM " + table + " WHERE " + whereClause;
		return queryForObject(query,conditionValues, clazz);
	}
	
	public <T> List<T> findAllBy(String whereClause, Object[] conditionValues, List<String> entityFields, Class<T> clazz) {
		String table = entityManager.getEntityData().get(clazz).getTableName();
		String query = "SELECT " + getDbFields(entityFields, clazz) + " FROM " + table + " WHERE " + whereClause;
		return queryForList(query, conditionValues, clazz);
	}
	
	public <T> Number save(T t) {
		try {
			String table = entityManager.getEntityData().get(t.getClass()).getTableName();
			String sql = "INSERT INTO " + table + 
					"(" + String.join(", ", entityManager.getEntityData().get(t.getClass()).getDbFields()) + ")"
					+ " VALUES"
					+ " (" 
					+ join(":", ", ", entityManager.getEntityData().get(t.getClass()).getDbFields())
					+ ")";
			
			
			MapSqlParameterSource paramSource = new MapSqlParameterSource();
			for (String dbField : entityManager.getEntityData().get(t.getClass()).getDbFields()) {
				 paramSource.addValue(dbField, entityManager.getEntityData().get(t.getClass()).getDbEntityFields().get(dbField).get(t));
			}
			
			KeyHolder holder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(sql, paramSource, holder);

			return holder.getKey();

		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String join(String prefix, String suffix, List<String> strings) {
		StringBuilder builder = new StringBuilder();
		builder.append(prefix + strings.get(0));
		for (int i = 1; i < strings.size(); i++) {
			builder.append(suffix + prefix + strings.get(i));
		}
		return builder.toString();
	}

	private <T> String getDbFields(List<String> entityFields, Class<T> clazz) {
		BiMap<String, String> map = entityManager.getEntityData().get(clazz).getEntityDbFields();
		StringBuilder builder = new StringBuilder();
		builder.append(map.get(entityFields.get(0)));
		int size = entityFields.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				builder.append(", " + map.get(entityFields.get(i)));
			}
		}

		return builder.toString();
	}
	
	private <T> Optional<T> queryForObject(String sql, Object[] conditionValues, Class<T> clazz) {
		T object = null;

		ResultSetExtractor<T> e = rs -> extractObject(rs, clazz);

		object = jdbcTemplate.getJdbcTemplate().query(sql, conditionValues, e);

		return Optional.ofNullable(object);
	}
	
	private <T> List<T> queryForList(String sql, Object[] conditionValues, Class<T> clazz) {
		ResultSetExtractor<List<T>> e = rs -> extractObjectList(rs, clazz);

		return jdbcTemplate.getJdbcTemplate().query(sql, conditionValues, e);
	}

	private <T> T extractObject(ResultSet rs, Class<T> clazz) throws SQLException, DataAccessException {
		T object = null;
		try {
			if(rs.next()) {
				object = getObject(rs, clazz);
			}
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	private <T> List<T> extractObjectList(ResultSet rs, Class<T> clazz) throws SQLException, DataAccessException {
		List<T> objects = new ArrayList<>();
		try {
			while(rs.next()) {
				objects.add(getObject(rs, clazz));
			}
			return objects;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	private <T> T getObject(ResultSet rs, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		
		T object = clazz.getDeclaredConstructor().newInstance();

		for (int i = 1; i <= columnCount; i++) {
			String name = rsmd.getColumnName(i);

			Object value = rs.getObject(name);
			
			Field field = entityManager.getEntityData().get(clazz).getDbEntityFields().get(name);
			field.set(object, value);
		}
		
		return object;
	}
}
