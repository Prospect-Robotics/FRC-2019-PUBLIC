//Copyright (c) 2018 Team 254

// Permission is hereby granted, free of charge,
// to any person obtaining a copy of this software
// and associated documentation files (the "Software"),
// to deal in the Software without restriction,
// including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons
// to whom the Software is furnished to do so.
package com.team2813.lib.util;

import java.util.List;

/**
 * Contains basic functions that are used often.
 */
public class Util {

	public static final double kEpsilon = 1e-12;

	/**
	 * Prevent this class from being instantiated.
	 */
	private Util() {
	}

	/**
	 * Limits the given input to the given magnitude.
	 */
	public static double limit(double v, double maxMagnitude) {
		return limit(v, -maxMagnitude, maxMagnitude);
	}

	public static double limit(double v, double min, double max) {
		return Math.min(max, Math.max(min, v));
	}
	
	public static int limitInt(int v, int min, int max) {
		return Math.min(max, Math.max(min, v));
	}

	public static double interpolate(double a, double b, double x) {
		x = limit(x, 0.0, 1.0);
		return a + (b - a) * x;
	}

	public static String concatenate(Object...objects){
		StringBuilder builder = new StringBuilder();
		for(Object object : objects){
			builder.append(object);
		}
		return builder.toString();
	}

	public static String concatenateDelimeter(String delimeter, Object...objects){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < objects.length; i++){
			builder.append(objects[i]);
			if(i + 1 < objects.length) builder.append(delimeter);
		}
		return builder.toString();
	}

	public static boolean epsilonEquals(double a, double b, double epsilon) {
		return (a - epsilon <= b) && (a + epsilon >= b);
	}

	public static boolean epsilonEquals(double a, double b) {
		return epsilonEquals(a, b, kEpsilon);
	}

    public static boolean allCloseTo(final List<Double> list, double value, double epsilon) {
        boolean result = true;
        for (Double value_in : list) {
            result &= epsilonEquals(value_in, value, epsilon);
        }
        return result;
    }
	
	public static double distance(double a, double b) {
		return Math.abs(a-b);
	}
	
	public static boolean epsilonEquals(int a, int b, int epsilon) {
		return (a - epsilon <= b) && (a + epsilon >= b);
	}
}
