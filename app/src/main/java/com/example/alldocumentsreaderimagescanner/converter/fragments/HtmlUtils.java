package com.example.alldocumentsreaderimagescanner.converter.fragments;

public class HtmlUtils {
    public static String getLinkFromPath(String path) {
        return "<a href=\"" + path + "\">" + path + "</a>";
    }
}
