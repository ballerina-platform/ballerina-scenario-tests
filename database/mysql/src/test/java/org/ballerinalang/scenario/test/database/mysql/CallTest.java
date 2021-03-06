/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.scenario.test.database.mysql;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BDecimal;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BValueArray;
import org.ballerinalang.scenario.test.common.ScenarioTestBase;
import org.ballerinalang.scenario.test.common.database.DatabaseUtil;
import org.ballerinalang.scenario.test.database.util.AssertionUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

/**
 * Contains `call` remote function tests.
 */
@Test(groups = Constants.MYSQL_TESTNG_GROUP)
public class CallTest extends ScenarioTestBase {
    private CompileResult callCompilerResult;
    private String jdbcUrl;
    private String userName;
    private String password;
    private Path resourcePath;
    private static long date;
    private static long time;
    private static long datetime;
    private static long timestamp;

    @BeforeClass
    public void setup() throws Exception {
        Properties deploymentProperties = getDeploymentProperties();
        jdbcUrl = deploymentProperties.getProperty(Constants.MYSQL_JDBC_URL_KEY) + "/testdb";
        userName = deploymentProperties.getProperty(Constants.MYSQL_JDBC_USERNAME_KEY);
        password = deploymentProperties.getProperty(Constants.MYSQL_JDBC_PASSWORD_KEY);

        ConfigRegistry registry = ConfigRegistry.getInstance();
        HashMap<String, String> configMap = new HashMap<>(3);
        configMap.put(Constants.MYSQL_JDBC_URL_KEY, jdbcUrl);
        configMap.put(Constants.MYSQL_JDBC_USERNAME_KEY, userName);
        configMap.put(Constants.MYSQL_JDBC_PASSWORD_KEY, password);
        registry.initRegistry(configMap, null, null);

        resourcePath = Paths.get("src", "test", "resources").toAbsolutePath();
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "ddl-select-update-test.sql"));
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "ddl-call-test.sql"));
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "dml-call-test.sql"));
        callCompilerResult = BCompileUtil
                .compile(Paths.get(resourcePath.toString(), "bal-src", "call-test.bal").toString());
        setupDateTimeData();
    }

    @Test(description = "Test numeric type In params")
    public void testCallInParamNumericTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInParamNumericTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
    }

    @Test(description = "Test numeric type OUT params")
    public void testCallOutParamIntegerTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallOutParamIntegerTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray integerValues = (BValueArray) returns[1];
        Assert.assertTrue(((BBoolean) integerValues.getRefValue(0)).booleanValue());
        Assert.assertEquals(((BInteger) integerValues.getRefValue(1)).intValue(), 126);
        Assert.assertEquals(((BInteger) integerValues.getRefValue(2)).intValue(), 32765);
        Assert.assertEquals(((BInteger) integerValues.getRefValue(3)).intValue(), 8388603);
        Assert.assertEquals(((BInteger) integerValues.getRefValue(4)).intValue(), 2147483644);
        Assert.assertEquals(((BInteger) integerValues.getRefValue(5)).intValue(), 2147483649L);
    }

    @Test(description = "Test numeric type INOUT params")
    public void testCallInOutParamIntegerTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInOutParamIntegerTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray numericValues = (BValueArray) returns[1];
        Assert.assertTrue(((BBoolean) numericValues.getRefValue(0)).booleanValue());
        Assert.assertEquals(((BInteger) numericValues.getRefValue(1)).intValue(), 126);
        Assert.assertEquals(((BInteger) numericValues.getRefValue(2)).intValue(), 32765);
        Assert.assertEquals(((BInteger) numericValues.getRefValue(3)).intValue(), 8388603);
        Assert.assertEquals(((BInteger) numericValues.getRefValue(4)).intValue(), 2147483644);
        Assert.assertEquals(((BInteger) numericValues.getRefValue(5)).intValue(), 2147483649L);
    }

    @Test(description = "Test fixed point type OUT params")
    public void testCallOutParamFixedPointTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallOutParamFixedPointTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray numericValues = (BValueArray) returns[1];
        Assert.assertEquals(((BDecimal) numericValues.getRefValue(0)).floatValue(), 143.78);
        Assert.assertEquals(((BDecimal) numericValues.getRefValue(1)).floatValue(), 1034.789);
    }

    @Test(description = "Test fixed point type INOUT params")
    public void testCallInOutParamFixedPointTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInOutParamFixedPointTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray numericValues = (BValueArray) returns[1];
        Assert.assertEquals(((BDecimal) numericValues.getRefValue(0)).floatValue(), 143.78);
        Assert.assertEquals(((BDecimal) numericValues.getRefValue(1)).floatValue(), 1034.789);
    }

    @Test(description = "Test string type OUT params")
    public void testCallOutParamStringTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallOutParamStringTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray stringValues = (BValueArray) returns[1];
        String[] fieldValues = {
                "Char Column", "Varchar column", "TinyText column", "Text column", "MediumText column",
                "LongText column", "A", "X"
        };
        for (int i = 0; i < stringValues.size(); i++) {
            Assert.assertEquals(stringValues.getRefValue(i).stringValue(), fieldValues[i]);
        }
    }

    @Test(description = "Test string type INOUT params")
    public void testCallInOutParamStringTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInOutParamStringTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        Assert.assertTrue(returns[1] instanceof BValueArray, "Invalid type");
        BValueArray stringValues = (BValueArray) returns[1];
        String[] fieldValues = {
                "Char Column", "Varchar column", "TinyText column", "Text column", "MediumText column",
                "LongText column", "A", "X"
        };
        for (int i = 0; i < stringValues.size(); i++) {
            Assert.assertEquals(stringValues.getRefValue(i).stringValue(), fieldValues[i]);
        }
    }

    @Test(description = "Test datetime type OUT params")
    public void testCallOutParamDateTimeValues() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallOutParamDateTimeValues");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        BValueArray valueArray = (BValueArray) returns[1];
        AssertionUtil.assertDateStringValues(valueArray, date, time, datetime, timestamp);
    }

    @Test(description = "Test datetime type INOUT params")
    public void testCallInOutParamDateTimeValues() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInOutParamDateTimeValues");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        BValueArray valueArray = (BValueArray) returns[1];
        AssertionUtil.assertDateStringValues(valueArray, date, time, datetime, timestamp);
    }

    @Test(description = "Test complex type OUT params")
    public void testCallOutParamComplexTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallOutParamComplexTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        BValueArray complexTypes = (BValueArray) returns[1];
        String[] expectedValues = {
                "Binary Column", "varbinary Column", "VGlueUJsb2IgQ29sdW1u", "QmxvYiBDb2x1bW4=",
                "TWVkaXVtQmxvYiBDb2x1bW4=", "TG9uZ0Jsb2IgQ29sdW1u"
        };

        for (int i = 0; i < expectedValues.length; i++) {
            String actualValue = complexTypes.getBValue(i).stringValue();
            if (i == 0) {
                // When BINARY values are stored, they are right-padded with the pad value to the specified length.
                // The pad value is 0x00 (the zero byte). Values are right-padded with 0x00 on insert, and no
                // trailing bytes are removed on select.
                actualValue = actualValue.trim();
            }
            Assert.assertEquals(actualValue, expectedValues[i]);
        }
    }

    @Test(description = "Test complex type OUT params")
    public void testCallInOutParamComplexTypes() {
        BValue[] returns = BRunUtil.invoke(callCompilerResult, "testCallInOutParamComplexTypes");
        AssertionUtil.assertCallQueryReturnValue(returns[0]);
        BValueArray complexTypes = (BValueArray) returns[1];
        String[] expectedValues = {
                "Binary Column", "varbinary Column", "VGlueUJsb2IgQ29sdW1u", "QmxvYiBDb2x1bW4=",
                "TWVkaXVtQmxvYiBDb2x1bW4=", "TG9uZ0Jsb2IgQ29sdW1u"
        };

        for (int i = 0; i < expectedValues.length; i++) {
            String actualValue = complexTypes.getBValue(i).stringValue();
            if (i == 0) {
                // When BINARY values are stored, they are right-padded with the pad value to the specified length.
                // The pad value is 0x00 (the zero byte). Values are right-padded with 0x00 on insert, and no
                // trailing bytes are removed on select.
                actualValue = actualValue.trim();
            }
            Assert.assertEquals(actualValue, expectedValues[i]);
        }
    }


    private void setupDateTimeData() {
        BValue[] args = new BValue[4];
        Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 23);
        date = cal.getTimeInMillis();
        args[0] = new BInteger(date);

        cal.clear();
        cal.set(Calendar.HOUR, 14);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 23);
        time = cal.getTimeInMillis();
        args[1] = new BInteger(time);

        cal.clear();
        cal.set(Calendar.HOUR, 16);
        cal.set(Calendar.MINUTE, 33);
        cal.set(Calendar.SECOND, 55);
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        datetime = cal.getTimeInMillis();
        args[2] = new BInteger(datetime);

        cal.clear();
        cal.set(Calendar.HOUR, 02);
        cal.set(Calendar.MINUTE, 33);
        cal.set(Calendar.SECOND, 55);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        timestamp = cal.getTimeInMillis();
        args[3] = new BInteger(timestamp);

        BRunUtil.invoke(callCompilerResult, "setUpDatetimeData", args);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        if (callCompilerResult != null) {
            BRunUtil.invoke(callCompilerResult, "stopDatabaseClient");
        }
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "cleanup-select-update-test.sql"));
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "cleanup-call-test.sql"));
    }
}
