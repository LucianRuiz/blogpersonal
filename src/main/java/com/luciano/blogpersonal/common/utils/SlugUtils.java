package com.luciano.blogpersonal.common.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    //Busca cualquier elemento que no sea alfanumerico
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");

    //Busca cualquier espacio en blanco
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    //Busca guiones consecutivos
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");


    private SlugUtils() {
        throw new IllegalStateException("No instanciar,es una Utility class");
    }



    /**
     * Genera un slug a partir de un texto.
     * Convierte a minúsculas, reemplaza espacios y caracteres especiales por guiones,
     * elimina guiones múltiples y guiones al inicio o final del texto.
     * @Param text El texto a convertir en slug
     * @return EL slug generado
     */

    public static String generateSlug(String text){
        if (text == null){
            return null;
        }

        //Normaliza el texto a un estado donde no hay tildes y todos los caracteres están en minúscula
        String slug = Normalizer
                .normalize(text, Normalizer.Form.NFD)//Elimina tildes
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") //Excepción para tildes
                .toLowerCase(Locale.ROOT); //Ni un idioma en específico

        //Reemplaza los caracteres no alfanúmericos por guiones
        slug = NONLATIN.matcher(slug).replaceAll("-");
        //Reemplaza los espacios en blanco por guiones
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        //Elimina los guiones multiples dejando solo uno
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        //Elimina guiones que esten al principio o al final
        slug = slug.replaceAll("^-|-$", "");

        return slug;

    }

    /**
     * Genera un slug único a partir de un texto base.
     * Si el slug ya existe, agrega un sufijo numérico hasta encontrar uno único.
     * @param text El texto base para generar el slug
     * @param slugExistsFunction Función que verifica si un slug ya existe
     * @return El slug único generado
     */
    public static String generateUniqueSlug(String text, SlugExistsFunction slugExistsFunction) {
        if (text == null) {
            return null;
        }

        String baseSlug = generateSlug(text);
        String uniqueSlug = baseSlug;
        int counter = 1;

        // Verifica si el slug existe y agrega un sufijo numérico si es necesario
        while (slugExistsFunction.exists(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        return uniqueSlug;
    }

    /**
     * Interfaz funcional para verificar si un slug existe
     */
    @FunctionalInterface
    public interface SlugExistsFunction {
        boolean exists(String slug);
    }

}
