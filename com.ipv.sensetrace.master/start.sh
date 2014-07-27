#!/bin/bash
export LD_LIBRARY_PATH=/usr/local/lib/swipl-6.6.1/lib/x86_64-linux:/usr/local/lib/swipl-6.6.1/bin/x86_64-linux

java -Declipse.ignoreApp=true -Dosgi.noShutdown=true -jar  org.eclipse.osgi_3.9.1.v20140110-1610.jar os linux -ws gtk -arch x86_64 -consoleLog -console
