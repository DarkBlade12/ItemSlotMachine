package com.darkblade12.itemslotmachine.core.replacer;

import java.util.ArrayList;
import java.util.List;

public final class Replacer {
    private List<Replacement<?>> replacements;

    Replacer(List<Replacement<?>> replacements) {
        if(replacements.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a replacer without replacements");
        }
        
        this.replacements = replacements;
    }

    public static ReplacerBuilder builder() {
        return new ReplacerBuilder();
    }

    public String replaceAll(String text) {
        String result = text;
        for (Replacement<?> repl : replacements) {
            result = repl.applyTo(result);
        }
        return result;
    }

    public static final class ReplacerBuilder {
        private List<Replacement<?>> replacements;

        ReplacerBuilder() {
            replacements = new ArrayList<Replacement<?>>();
        }

        private boolean isDuplicate(Replacement<?> replacement) {
            String p1 = replacement.getPlaceholder().getPattern().pattern();
            for (Replacement<?> repl : replacements) {
                String p2 = repl.getPlaceholder().getPattern().pattern();
                if (p1.equals(p2)) {
                    return true;
                }
            }

            return false;
        }

        public ReplacerBuilder with(Replacement<?> replacement) {
            if(isDuplicate(replacement)) {
                throw new IllegalArgumentException("A replacement with the same pattern has already been added");
            }
            
            replacements.add(replacement);
            return this;
        }

        public <T> ReplacerBuilder with(Placeholder<T> placeholder, T value) {
            return with(new Replacement<T>(placeholder, value));
        }

        public Replacer build() {
            return new Replacer(replacements);
        }
    }
}
