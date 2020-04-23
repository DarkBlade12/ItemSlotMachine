package com.darkblade12.itemslotmachine.core.replacer;

import java.util.regex.Pattern;

public class Placeholder<T> {
    private final Pattern pattern;

    public Placeholder(Pattern pattern) {
        this.pattern = pattern;
    }
    
    public Placeholder(String regex) {
        this(Pattern.compile(regex));
    }

    public Placeholder(String regex, int flags) {
        this(Pattern.compile(regex, flags));
    }

    public String replaceAll(String text, T value) {
        return pattern.matcher(text).replaceAll(getAsString(value));
    }
    
    public String getAsString(T value) {
        return String.valueOf(value);
    }
    
    public Pattern getPattern() {
        return pattern;
    }
}
