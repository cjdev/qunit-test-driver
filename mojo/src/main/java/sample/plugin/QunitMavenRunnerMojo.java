package sample.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Says "Hi" to the user.
 * @phase test
 * @goal test
 */
public class QunitMavenRunnerMojo extends AbstractMojo {
    
    /**
     * @parameter default-value="${basedir}
     * @readonly
     * @required
     */
    private File basedir;
    
    public void execute() throws MojoFailureException {
        if(shouldSkipTests()) return;
        final List<String> filesRun = new ArrayList<String>();
        final List<String> problems = new QunitMavenRunner().run(basedir, new QunitMavenRunner.Listener() {
            @Override
            public void runningTest(String relativePath) {
                getLog().info("Running " + relativePath);
                filesRun.add(relativePath);
            }
        });
        
        if(!problems.isEmpty()){
            StringBuffer problemsString = new StringBuffer();
            
            for(String next : problems){
                problemsString.append(next);
                problemsString.append('\n');
            }

            throw new MojoFailureException(problemsString.toString());
        }else{
            getLog().info("Ran " + filesRun.size() + " QUnit html files");
        }
    }

    private boolean shouldSkipTests() {
        boolean skipTests = false;
        
        final String[] skipFlags = {"maven.test.skip", "skipTests", "qunit.skip"};
        
        for(String skipFlag : skipFlags){
            String value = System.getProperty(skipFlag);
            if(value!=null && !value.trim().toLowerCase().equals("false")){
                getLog().warn("###########################################################################");
                getLog().warn("## Skipping Qunit tests because the \"" + skipFlag + "\" property is set.");
                getLog().warn("###########################################################################");
                skipTests = true;
                break;
            }
        }
        return skipTests;
    }
}

