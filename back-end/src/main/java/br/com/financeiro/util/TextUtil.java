package br.com.financeiro.util;

public final class TextUtil {

    private TextUtil() {
    }

    public static String trimToNull(String valor) {
        if (valor == null) {
            return null;
        }
        String aparado = valor.trim();
        return aparado.isEmpty() ? null : aparado;
    }
}