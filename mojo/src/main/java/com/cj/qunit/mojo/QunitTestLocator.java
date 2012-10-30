package com.cj.qunit.mojo;

import static com.cj.qunit.mojo.FilesystemFunctions.scanFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.cj.qunit.mojo.FilesystemFunctions.FileVisitor;


public class QunitTestLocator {
    public static class LocatedTest {
        public final String name;
        public final String relativePath;

        private LocatedTest(String name, String relativePath) {
            super();
            this.name = name;
            this.relativePath = relativePath;
        }

    }

    public List<LocatedTest> locateTests(final File projectDirectory){

        final List<LocatedTest> results = new ArrayList<QunitTestLocator.LocatedTest>();

        File htmlFiles = new File(projectDirectory,  "src/test");

        scanFiles(htmlFiles, new FileVisitor(){
            @Override
            public void visit(File path) {
                final String name = path.getName();
                final String relativePath = path.getAbsolutePath().replaceAll(Pattern.quote(projectDirectory.getAbsolutePath()), "").substring(1);
                
                if(name.matches(".*Qunit.*\\.html")){
                    results.add(new LocatedTest(relativePath, relativePath));
                }else if(name.endsWith(".qunit-test.js")){
                    results.add(new LocatedTest(relativePath, relativePath + ".Qunit.html"));
                }
            }
        });

        return results;
    }
}
