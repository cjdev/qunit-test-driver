package com.cj.qunitTestDriver.config

import com.cj.qunitTestDriver.QUnitTestDriver;

/**
 * Specifies the root of the server that Jetty will be given.
 */
class PathMapping implements Configuration{
    String pattern
	String directory

    public PathMapping(String pattern, String directory){
        this.pattern = pattern
		this.directory = directory
    }

    void configure(QUnitTestDriver runner){
		List<String> pathMapping = runner.pathMappings.get(pattern)
		if(pathMapping == null) {
			pathMapping = []
			runner.pathMappings.putAt(pattern, pathMapping)
		}
		
        pathMapping.add(directory)
    }
}
