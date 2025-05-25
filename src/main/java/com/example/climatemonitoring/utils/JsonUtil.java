package com.example.climatemonitoring.utils;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static <T> T readJson(String filePath, Class<T> clazz) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public static <T> List<T> readJsonList(String filePath, Type typeOfT) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeOfT);
        }
    }

    public static <T> void writeJson(String filePath, T data) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    public static <T> void writeJsonList(String filePath, List<T> data) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }
}