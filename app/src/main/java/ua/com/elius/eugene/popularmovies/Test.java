package ua.com.elius.eugene.popularmovies;

import java.math.BigDecimal;
import java.util.Date;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table = "test", provider = "TestProvider")
public class Test {

    @SimpleSQLColumn("col_str")
    public String myString;

    @SimpleSQLColumn(value = "col_int", primary = true)
    public int anInt;

    @SimpleSQLColumn("col_integer")
    public int myinteger;

    @SimpleSQLColumn("col_short")
    public int myshort;

    @SimpleSQLColumn("col_short2")
    public int myShort;

    @SimpleSQLColumn("col_long")
    public long mylong;

    @SimpleSQLColumn("col_long2")
    public int myLong;

    @SimpleSQLColumn("col_double")
    public long mydouble;

    @SimpleSQLColumn("col_double2")
    public int myDouble;

    @SimpleSQLColumn("col_float")
    public long myfloat;

    @SimpleSQLColumn("col_float2")
    public int myFloat;

    @SimpleSQLColumn("col_bigdecimal")
    public BigDecimal bigD;

    @SimpleSQLColumn("col_bool")
    public boolean mybool;

    @SimpleSQLColumn("col_bool2")
    public boolean myBool;

    @SimpleSQLColumn("col_date")
    public Date mydateCol;
}