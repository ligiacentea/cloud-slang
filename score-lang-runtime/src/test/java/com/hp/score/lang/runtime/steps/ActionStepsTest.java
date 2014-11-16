/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package com.hp.score.lang.runtime.steps;

import com.hp.oo.sdk.content.plugin.GlobalSessionObject;
import com.hp.oo.sdk.content.plugin.SerializableSessionObject;
import com.hp.score.api.execution.ExecutionParametersConsts;
import com.hp.score.events.ScoreEvent;
import com.hp.score.lang.ExecutionRuntimeServices;
import com.hp.score.lang.runtime.env.ReturnValues;
import com.hp.score.lang.runtime.env.RunEnvironment;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.hp.score.lang.entities.ActionType.JAVA;
import static com.hp.score.lang.entities.ActionType.PYTHON;
import static com.hp.score.lang.entities.ScoreLangConstants.EVENT_ACTION_END;
import static com.hp.score.lang.entities.ScoreLangConstants.EVENT_ACTION_ERROR;
import static org.mockito.Mockito.mock;

/**
 * Date: 10/31/2014
 *
 * @author Bonczidai Levente
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ActionStepsTest.Config.class)
public class ActionStepsTest {

    private static final long DEFAULT_TIMEOUT = 10000;

    @Autowired
    private ActionSteps actionSteps;
	ExecutionRuntimeServices executionRuntimeServicesMock = mock(ExecutionRuntimeServices.class);

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doActionJavaTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("name", "nameTest");
        initialCallArguments.put("role", "roleTest");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
		actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(), "doJavaSampleAction", executionRuntimeServicesMock, null);

        //construct expected outputs
        Map<String, String> expectedOutputs = new HashMap<>();
        expectedOutputs.put("name", "nameTest");
        expectedOutputs.put("role", "roleTest");

        //extract actual outputs
        ReturnValues actualReturnValues = runEnv.removeReturnValues();
        Map<String, String> actualOutputs = actualReturnValues.getOutputs();

        //verify matching
        Assert.assertEquals("Java action outputs are not as expected", expectedOutputs, actualOutputs);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionWrongClassTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        ExecutionRuntimeServices runtimeServices = new ExecutionRuntimeServices();

        //invoke doAction
        actionSteps.doAction(runEnv, new HashMap<String, Object>(), JAVA, "MissingClassName", "doJavaSampleAction", runtimeServices, null);

        Collection<ScoreEvent> events = runtimeServices.getEvents();

        Assert.assertFalse(events.isEmpty());
        ScoreEvent actionErrorEvent = null;
        ScoreEvent actionEndEvent = null;
        for(ScoreEvent event:events){
            if(event.getEventType().equals(EVENT_ACTION_ERROR)){
                actionErrorEvent = event;
            } else if(event.getEventType().equals(EVENT_ACTION_END)){
                actionEndEvent = event;
            }
        }
        Assert.assertNotNull(actionErrorEvent);
        Assert.assertNotNull(actionEndEvent);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionWrongReturnTypeTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        ExecutionRuntimeServices runtimeServices = new ExecutionRuntimeServices();

        //invoke doAction
        actionSteps.doAction(runEnv, new HashMap<String, Object>(), JAVA, ContentTestActions.class.getName(), "doJavaActionWrongReturnType", runtimeServices, null);

        Collection<ScoreEvent> events = runtimeServices.getEvents();

        Assert.assertFalse(events.isEmpty());
        ScoreEvent actionErrorEvent = null;
        ScoreEvent actionEndEvent = null;
        for(ScoreEvent event:events){
            if(event.getEventType().equals(EVENT_ACTION_ERROR)){
                actionErrorEvent = event;
            } else if(event.getEventType().equals(EVENT_ACTION_END)){
                actionEndEvent = event;
            }
        }
        Assert.assertNotNull(actionErrorEvent);
        Assert.assertNotNull(actionEndEvent);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doActionJavaMissingInputTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("name", "nameTest");
        // missing role
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(), "doJavaSampleAction", executionRuntimeServicesMock, null);

        //construct expected outputs
        Map<String, String> expectedOutputs = new HashMap<>();
        expectedOutputs.put("name", "nameTest");
        expectedOutputs.put("role", null);

        //extract actual outputs
        ReturnValues actualReturnValues = runEnv.removeReturnValues();
        Map<String, String> actualOutputs = actualReturnValues.getOutputs();

        //verify matching
        Assert.assertEquals("Java action outputs are not as expected", expectedOutputs, actualOutputs);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doActionJavaMissingActionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("name", "nameTest");
        initialCallArguments.put("role", "roleTest");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(), "doJavaSampleAction_NOT_FOUND", executionRuntimeServicesMock, null);

        //construct expected outputs
        Map<String, String> expectedOutputs = new HashMap<>();

        //extract actual outputs
        ReturnValues actualReturnValues = runEnv.removeReturnValues();
        Map<String, String> actualOutputs = actualReturnValues.getOutputs();

        //verify matching
        Assert.assertEquals("Java action output should be empty map", expectedOutputs, actualOutputs);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doActionJavaMissingActionAnnotationTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("name", "nameTest");
        initialCallArguments.put("role", "roleTest");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(), "doJavaActionMissingAnnotation", executionRuntimeServicesMock, null);

        //construct expected outputs
        Map<String, String> expectedOutputs = new HashMap<>();

        //extract actual outputs
        ReturnValues actualReturnValues = runEnv.removeReturnValues();
        Map<String, String> actualOutputs = actualReturnValues.getOutputs();

        //verify matching
        Assert.assertEquals("Java action output should be empty map", expectedOutputs, actualOutputs);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionGetKeyFromNonSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        HashMap<String, Object> nonSerializableExecutionData = new HashMap<>();
        GlobalSessionObject<ContentTestActions.NonSerializableObject> sessionObject = new GlobalSessionObject<>();
        ContentTestActions.NonSerializableObject employee = new ContentTestActions.NonSerializableObject("John");
        sessionObject.setResource(new ContentTestActions.NonSerializableSessionResource(employee));
        nonSerializableExecutionData.put("name", sessionObject);

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(),
                "getNameFromNonSerializableSession", executionRuntimeServicesMock, null);

        Map<String, String> outputs = runEnv.removeReturnValues().getOutputs();
        Assert.assertTrue(outputs.containsKey("name"));
        Assert.assertEquals("John", outputs.get("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionSetKeyOnNonSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        HashMap<String, Object> nonSerializableExecutionData = new HashMap<>();
        GlobalSessionObject<ContentTestActions.NonSerializableObject> sessionObject = new GlobalSessionObject<>();
        ContentTestActions.NonSerializableObject employee = new ContentTestActions.NonSerializableObject("John");
        sessionObject.setResource(new ContentTestActions.NonSerializableSessionResource(employee));
        nonSerializableExecutionData.put("name", sessionObject);
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("value", "David");
        runEnv.putCallArguments(initialCallArguments);

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(),
                "setNameOnNonSerializableSession", executionRuntimeServicesMock, null);

        Map<String, String> outputs = runEnv.removeReturnValues().getOutputs();
        Assert.assertTrue(outputs.containsKey("name"));
        Assert.assertEquals("David", outputs.get("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionGetNonExistingKeyFromNonSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        HashMap<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, JAVA, ContentTestActions.class.getName(),
                "getNameFromNonSerializableSession", executionRuntimeServicesMock, null);

        Map<String, String> outputs = runEnv.removeReturnValues().getOutputs();
        Assert.assertTrue(outputs.containsKey("name"));
        Assert.assertNull(outputs.get("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionGetKeyFromSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        HashMap<String, SerializableSessionObject> serializableExecutionData = new HashMap<>();
        SerializableSessionObject sessionObject = new SerializableSessionObject();
        sessionObject.setName("John");
        serializableExecutionData.put("name", sessionObject);
        runEnv.getSerializableDataMap().putAll(serializableExecutionData);
        runEnv.putCallArguments(initialCallArguments);

        //invoke doAction
        actionSteps.doAction(runEnv, new HashMap<String, Object>(), JAVA, ContentTestActions.class.getName(),
                "getNameFromSerializableSession", executionRuntimeServicesMock, null);

        Map<String, String> outputs = runEnv.removeReturnValues().getOutputs();
        Assert.assertTrue(outputs.containsKey("name"));
        Assert.assertEquals("John", outputs.get("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionGetNonExistingKeyFromSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        HashMap<String, Object> serializableExecutionData = new HashMap<>();
        initialCallArguments.put(ExecutionParametersConsts.SERIALIZABLE_SESSION_CONTEXT, serializableExecutionData);
        runEnv.putCallArguments(initialCallArguments);

        //invoke doAction
        actionSteps.doAction(runEnv, new HashMap<String, Object>(), JAVA, ContentTestActions.class.getName(),
                "getNameFromSerializableSession", executionRuntimeServicesMock, null);

        Map<String, String> outputs = runEnv.removeReturnValues().getOutputs();
        Assert.assertTrue(outputs.containsKey("name"));
        Assert.assertNull(outputs.get("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doJavaActionGetNonExistingKeyFromNonExistingSerializableSessionTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        runEnv.putCallArguments(initialCallArguments);

        //invoke doAction
        actionSteps.doAction(runEnv, new HashMap<String, Object>(), JAVA, ContentTestActions.class.getName(),
                "getNameFromSerializableSession", executionRuntimeServicesMock, null);

        Map<String, SerializableSessionObject> serializableSessionMap = runEnv.getSerializableDataMap();
        Assert.assertTrue(serializableSessionMap.containsKey("name"));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void doActionPythonTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("host", "localhost");
        initialCallArguments.put("port", "8080");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        String userPythonScript = "import os\n" +
                "print host\n" +
                "print port\n" +
                "os.system(\"ping -c 1 \" + host)\n" +
                "url = 'http://' + host + ':' + str(port)\n" +
                "url2 = url + '/oo'\n" +
                "another = 'just a string'\n" +
//                we can also change..
                "port = 8081\n" +
                "print url";

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, PYTHON, "", "", executionRuntimeServicesMock, userPythonScript);

        //construct expected outputs
        Map<String, String> expectedOutputs = new HashMap<>();
        expectedOutputs.put("host", "localhost");
        expectedOutputs.put("port", "8081");
        expectedOutputs.put("url", "http://localhost:8080");
        expectedOutputs.put("url2", "http://localhost:8080/oo");
        expectedOutputs.put("another", "just a string");

        //extract actual outputs
        ReturnValues actualReturnValues = runEnv.removeReturnValues();
        Map<String, String> actualOutputs = actualReturnValues.getOutputs();

        //verify matching
        Assert.assertEquals("Python action outputs are not as expected", expectedOutputs, actualOutputs);
    }

    @Test (timeout = DEFAULT_TIMEOUT)
    public void doActionPythonMissingInputsTest() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        //missing inputs
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        String userPythonScript = "import os\n" +
                "print host\n" +
                "print port\n" +
                "os.system(\"ping -c 1 \" + host)\n" +
                "url = 'http://' + host + ':' + str(port)\n" +
                "url2 = url + '/oo'\n" +
                "another = 'just a string'\n" +
                "port = 8081\n" +
                "print url";

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, PYTHON, "", "", executionRuntimeServicesMock, userPythonScript);

        Map<String, String> actionOutputs = runEnv.removeReturnValues().getOutputs();
        Map<String, String> expectedOutputs = new HashMap<>();
        Assert.assertEquals("Action should return empty map", expectedOutputs, actionOutputs);
    }

    @Test (timeout = DEFAULT_TIMEOUT)
    public void doActionPythonEmptyScript() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("host", "localhost");
        initialCallArguments.put("port", "8080");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, PYTHON, "", "", executionRuntimeServicesMock, "");

        Map<String, String> actionOutputs = runEnv.removeReturnValues().getOutputs();
        Map<String, String> expectedOutputs = new HashMap<>();
        Assert.assertEquals("Action should return empty map", expectedOutputs, actionOutputs);
    }

    @Test (timeout = DEFAULT_TIMEOUT)
    public void doActionPythonMissingScript() {
        //prepare doAction arguments
        RunEnvironment runEnv = new RunEnvironment();
        Map<String, Serializable> initialCallArguments = new HashMap<>();
        initialCallArguments.put("host", "localhost");
        initialCallArguments.put("port", "8080");
        runEnv.putCallArguments(initialCallArguments);

        Map<String, Object> nonSerializableExecutionData = new HashMap<>();

        //invoke doAction
        actionSteps.doAction(runEnv, nonSerializableExecutionData, PYTHON, "", "", executionRuntimeServicesMock, null);

        Map<String, String> actionOutputs = runEnv.removeReturnValues().getOutputs();
        Map<String, String> expectedOutputs = new HashMap<>();
        Assert.assertEquals("Action should return empty map", expectedOutputs, actionOutputs);
    }

    @Configuration
    static class Config {

        @Bean
        public ActionSteps actionSteps() {
            return new ActionSteps();
        }

        @Bean
        public PythonInterpreter pythonInterpreter() {
            return new PythonInterpreter();
        }

    }
}