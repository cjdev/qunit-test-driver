package sample.plugin;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class QunitMavenRunnerTest {

    @Test
    public void findsFilesUnderTheSrcTestDirectory() throws Exception {
        // given
        File projectDirectory = tempDirectory();
        File srcMainHtmlDirectory = new File(projectDirectory, "src/test/whatever");
        srcMainHtmlDirectory.mkdirs();
        
        for(String name : new String[]{"SomeQunitTest.html", "jquery-1.8.2.min.js", "qunit-1.10.0.css", "qunit-1.10.0.js"}){
            copyToDiskFromClasspath(srcMainHtmlDirectory, name);
        }
        
        QunitMavenRunner runner = new QunitMavenRunner();
        FakeLog log = new FakeLog();
        
        // when
        List<String> problems;
        Exception t;
        try {
            problems = runner.run(projectDirectory, log);
            t = null;
        } catch (Exception e) {
            t = e;
            problems = Collections.emptyList();
        }
        
        // then
        System.out.println(srcMainHtmlDirectory.getAbsolutePath());
        Assert.assertTrue("The plugin should not blow up", t == null);
        Assert.assertEquals(0, problems.size());
        Assert.assertEquals(1, log.pathsRun.size());
        Assert.assertEquals("src/test/whatever/SomeQunitTest.html", log.pathsRun.get(0));
    }
    

    @Test
    public void theBuildFailsWhenATestFails() throws Exception {
        // given
        File projectDirectory = tempDirectory();
        File srcMainHtmlDirectory = new File(projectDirectory, "src/test/html");
        srcMainHtmlDirectory.mkdirs();
        
        for(String name : new String[]{"SomeFailingQunitTest.html", "jquery-1.8.2.min.js", "qunit-1.10.0.css", "qunit-1.10.0.js"}){
            copyToDiskFromClasspath(srcMainHtmlDirectory, name);
        }
        
        QunitMavenRunner runner = new QunitMavenRunner();
        FakeLog log = new FakeLog();
        
        // when
        List<String> problems;
        Throwable t;
        try {
            problems = runner.run(projectDirectory, log);
            t = null;
        } catch (Throwable e) {
            t = e;
            problems = Collections.emptyList();
        }
        
        // then
        Assert.assertNull("The build should not have failed", t);
        Assert.assertEquals(1, log.pathsRun.size());
        Assert.assertEquals("src/test/html/SomeFailingQunitTest.html", log.pathsRun.get(0));
        
        Assert.assertEquals(1, problems.size());
        String problem = problems.get(0);
        
        Assert.assertTrue(problem.startsWith("Problems found in 'src/test/html/SomeFailingQunitTest.html'"));
        Assert.assertTrue(problem.contains("module with failing test in it: this test left intentionally failing"));
        Assert.assertTrue(problem.contains("0 tests of 1 passed, 1 failed"));
    }
    
    private static class FakeLog implements QunitMavenRunner.Listener {
        List<String> pathsRun = new ArrayList<String>();
        
        @Override
        public void runningTest(String relativePath) {
            pathsRun.add(relativePath);
        }
        
    }
    
    private void copyToDiskFromClasspath(File srcMainHtmlDirectory, String name)
            throws IOException {
        FileUtils.write(new File(srcMainHtmlDirectory, name), readClasspathResourceAsString("/" + name));
    }

    private String readClasspathResourceAsString(String name) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(name));
    }
    
    private static File tempDirectory(){
        try {
            File d = File.createTempFile("whatever", ".dir");
            if(!d.delete() || !d.mkdir()){
                throw new RuntimeException("Could not create temporary directory at " + d.getAbsolutePath());
            }
            
            return d;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
