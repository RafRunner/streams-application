package br.rafaelsantana.builders;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvBuilder {

    private static Dotenv instance;

    public static Dotenv build() {
        if (instance == null) {
            instance = Dotenv.load();
        }
        return instance;
    }
}
