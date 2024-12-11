package com.dtflys.forest.test.converter.protobuf;


import com.google.protobuf.Parser;

import java.util.List;
import java.util.Map;

/**
 * @author YAKAX
 * @since 2020/12/18 20:58
 **/

public class Protobuf {
    public static final Parser<ProtobufProto.BaseData> PARSER = ProtobufProto.BaseData.parser();
    private double doubleVal;
    private float floatVal;
    private int int32Val;
    private long int64Val;
    private int uint32Val;
    private long uint64Val;
    private int sint32Val;
    private long sint64Val;
    private int fixed32Val;
    private long fixed64Val;
    private int sfixed32Val;
    private long sfixed64Val;
    private boolean boolVal;
    private String stringVal;
    private String bytesVal;

    private String enumVal;

    private List<String> reStrVal;
    private Map<String, Protobuf> mapVal;

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public void setFloatVal(float floatVal) {
        this.floatVal = floatVal;
    }

    public void setInt32Val(int int32Val) {
        this.int32Val = int32Val;
    }

    public void setInt64Val(long int64Val) {
        this.int64Val = int64Val;
    }

    public void setUint32Val(int uint32Val) {
        this.uint32Val = uint32Val;
    }

    public void setUint64Val(long uint64Val) {
        this.uint64Val = uint64Val;
    }

    public void setSint32Val(int sint32Val) {
        this.sint32Val = sint32Val;
    }

    public void setSint64Val(long sint64Val) {
        this.sint64Val = sint64Val;
    }

    public void setFixed32Val(int fixed32Val) {
        this.fixed32Val = fixed32Val;
    }

    public void setFixed64Val(long fixed64Val) {
        this.fixed64Val = fixed64Val;
    }

    public void setSfixed32Val(int sfixed32Val) {
        this.sfixed32Val = sfixed32Val;
    }

    public void setSfixed64Val(long sfixed64Val) {
        this.sfixed64Val = sfixed64Val;
    }

    public void setBoolVal(boolean boolVal) {
        this.boolVal = boolVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public void setBytesVal(String bytesVal) {
        this.bytesVal = bytesVal;
    }

    public void setEnumVal(String enumVal) {
        this.enumVal = enumVal;
    }

    public void setReStrVal(List<String> reStrVal) {
        this.reStrVal = reStrVal;
    }

    public void setMapVal(Map<String, Protobuf> mapVal) {
        this.mapVal = mapVal;
    }

    public double getDoubleVal() {
        return doubleVal;
    }

    public float getFloatVal() {
        return floatVal;
    }

    public int getInt32Val() {
        return int32Val;
    }

    public long getInt64Val() {
        return int64Val;
    }

    public int getUint32Val() {
        return uint32Val;
    }

    public long getUint64Val() {
        return uint64Val;
    }

    public int getSint32Val() {
        return sint32Val;
    }

    public long getSint64Val() {
        return sint64Val;
    }

    public int getFixed32Val() {
        return fixed32Val;
    }

    public long getFixed64Val() {
        return fixed64Val;
    }

    public int getSfixed32Val() {
        return sfixed32Val;
    }

    public long getSfixed64Val() {
        return sfixed64Val;
    }

    public boolean isBoolVal() {
        return boolVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public String getBytesVal() {
        return bytesVal;
    }

    public String getEnumVal() {
        return enumVal;
    }

    public List<String> getReStrVal() {
        return reStrVal;
    }

    public Map<String, Protobuf> getMapVal() {
        return mapVal;
    }
}
