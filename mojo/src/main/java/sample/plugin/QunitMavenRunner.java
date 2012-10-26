package sample.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.cj.qunitTestDriver.QUnitTestDriver;
import com.cj.qunitTestDriver.config.PathMapping;

public class QunitMavenRunner {
    public static interface Listener {
        void runningTest(String relativePath);
    }
    public List<String> run(final File projectDirectory, final Listener log) {
        String qUnitTestPath = "src/test";
        File htmlFiles = new File(projectDirectory, qUnitTestPath);
        
        final List<String> problems = new ArrayList<String>(); 
        
        scanFiles(htmlFiles, new FileVisitor(){
            @Override
            public void visit(File path) {
                final String name = path.getName();
                if(!name.matches(".*Qunit.*\\.html")) return;
                
                final String relativePath = path.getAbsolutePath().replaceAll(Pattern.quote(projectDirectory.getAbsolutePath()), "").substring(1);
                
                log.runningTest(relativePath);
                
                try {
                    QUnitTestDriver.run(relativePath, new com.cj.qunitTestDriver.config.Configuration[]{ new PathMapping("/",projectDirectory.getAbsolutePath())});                        
                } catch (Throwable m){
                    problems.add("Problems found in '" + relativePath +"':\n"+m.getMessage());
                }
            }
        });

        return problems;
    }
    
    private static interface FileVisitor{
        void visit(File path);
    }
    private static void scanFiles(File path, FileVisitor visitor){
        if(path.isDirectory()){
            for(File next : path.listFiles()){
                scanFiles(next, visitor);
            }
        }else{
            visitor.visit(path);
        }
    }
}
