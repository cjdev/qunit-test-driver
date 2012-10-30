package com.cj.qunit.mojo.http;

import java.io.File;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;

import com.cj.qunit.mojo.QunitTestLocator;


class TestListingResource extends HttpObject {
    private final File projectDirectory;
    
    public TestListingResource(String pathPattern, File projectDirectory) {
        super(pathPattern);
        this.projectDirectory = projectDirectory;
    }
    
    @Override
    public Response get(Request req) {

        StringBuffer html = new StringBuffer("<html><body><h1>Qunit Tests</h1>");
        
        for(QunitTestLocator.LocatedTest test: new QunitTestLocator().locateTests(projectDirectory)){
            html.append("<div><a href=\"" + test.relativePath + "\">" + test.name + "</a></div>");
        }
        
        html.append("</body></html");
        
        return OK(Html(html.toString()));
    }
}