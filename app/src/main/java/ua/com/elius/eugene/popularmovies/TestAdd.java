package ua.com.elius.eugene.popularmovies;

import java.math.BigDecimal;
import java.util.Date;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table = "test_add", provider = "TestProvider")
public class TestAdd {

    @SimpleSQLColumn("c_str")
    public String myString;

    @SimpleSQLColumn(value = "col_int", primary = true)
    public int anInt;

    @SimpleSQLColumn("c_integer")
    public int myinteger;

    @SimpleSQLColumn("c_short")
    public int myshort;

    @SimpleSQLColumn("c_short2")
    public int myShort;

    @SimpleSQLColumn("c_long")
    public long mylong;

    @SimpleSQLColumn("c_long2")
    public int myLong;

    @SimpleSQLColumn("c_double")
    public long mydouble;

    @SimpleSQLColumn("c_double2")
    public int myDouble;

    @SimpleSQLColumn("c_float")
    public long myfloat;

    @SimpleSQLColumn("c_float2")
    public int myFloat;

    @SimpleSQLColumn("c_bigdecimal")
    public BigDecimal bigD;

    @SimpleSQLColumn("c_bool")
    public boolean mybool;

    @SimpleSQLColumn("c_bool2")
    public boolean myBool;

    @SimpleSQLColumn("c_date")
    public Date mydateCol;
}
