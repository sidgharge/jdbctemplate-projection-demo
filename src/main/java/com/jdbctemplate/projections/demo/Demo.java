package com.jdbctemplate.projections.demo;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;


public class Demo {

	public static void main(String[] args) {
		StringJoiner joiner = new StringJoiner(", ", ":", "");
		List<String> list = Arrays.asList("Apple", "Banana", "Carrot");
		list.forEach(joiner::add);
		System.out.println(joiner.toString());
	}
}
