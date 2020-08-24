package com.dtflys.forest.http;

import java.util.*;

public class ForestHeaderMap {

    private final List<ForestHeader> headers;

    public ForestHeaderMap(List<ForestHeader> headers) {
        this.headers = headers;
    }

    public ForestHeaderMap() {
        this.headers = new LinkedList<>();
    }


    public int size() {
        return headers.size();
    }

    public String getValue(String name) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    public List<String> getValues(String name) {
        List<String> results = new ArrayList<>(2);
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                results.add(header.getValue());
            }
        }
        return Collections.unmodifiableList(results);
    }


    public ForestHeader getHeader(String name) {
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public List<ForestHeader> getHeaders(String name) {
        List<ForestHeader> results = new ArrayList<>(2);
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                results.add(header);
            }
        }
        return results;
    }


    public List<String> names() {
        List<String> results = new ArrayList<>(headers.size());
        for (ForestHeader header : headers) {
            results.add(header.getName());
        }
        return Collections.unmodifiableList(results);
    }


    public List<String> getValues() {
        List<String> results = new ArrayList<>(headers.size());
        for (ForestHeader header : headers) {
            results.add(header.getValue());
        }
        return Collections.unmodifiableList(results);
    }

    public void addHeader(ForestHeader header) {
        headers.add(header);
    }

    public void addHeader(String name, String value) {
        addHeader(new ForestHeader(name, value));
    }

    public void setHeader(String name, String value) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            header.setValue(value);
        } else {
            addHeader(name, value);
        }
    }


    public Iterator<ForestHeader> headerIterator() {
        return headers.iterator();
    }

    public void remove(String name) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            headers.remove(header);
        }
    }

}
