package com.darkblade12.itemslotmachine.plugin.replacer;

public class Replacement<T> {
    private Placeholder<T> placeholder;
    private T value;

    public Replacement(Placeholder<T> placeholder, T value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    public String applyTo(String text) {
        return placeholder.replaceAll(text, value);
    }

    public Placeholder<T> getPlaceholder() {
        return placeholder;
    }

    public T getValue() {
        return value;
    }
}
