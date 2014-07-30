#!/bin/bash 
#Please set path to org.eclipse.osgi...jar

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
export LD_LIBRARY_PATH=/usr/local/lib/swipl-6.6.1/lib/x86_64-linux:/usr/local/lib/swipl-6.6.1/bin/x86_64-linux

java -Declipse.ignoreApp=true -Dosgi.noShutdown=true -Dimport_from_dl=true -jar   /home/sensetrace/export/org.eclipse.osgi_3.9.1.v20140110-1610.jar os linux -ws gtk -arch x86_64 -consoleLog -console

