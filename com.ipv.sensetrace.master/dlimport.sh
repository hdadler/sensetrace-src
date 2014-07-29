#!/bin/bash


PATH="/home/pvsystem/sensetrace/export"
DL_FROM=folder

#At first check if sensetrace is allready running
#if /bin/ps ax | /bin/grep -v /bin/grep | /bin/grep "$SERVICE" > /dev/null
#then
 #   /bin/echo "$SERVICE allready running. Will not start import".
#else
 #   /bin/echo "$SERVICE is not running. Will start import."

export LD_LIBRARY_PATH=/usr/local/lib/swipl-6.6.1/lib/x86_64-linux:/usr/local/lib/swipl-6.6.1/bin/x86_64-linux

/usr/bin/java -Declipse.ignoreApp=true -Dosgi.noShutdown=true -jar  org.eclipse.osgi_3.9.1.v20140110-1610.jar os linux -ws gtk -arch x86_64 -consoleLog -console 5555 &

	/bin/sleep 10;

	echo "Connect to console..."
    /usr/bin/expect -c "spawn /usr/bin/telnet localhost 5555 
	expect \"'^]'.\" 
	send \"import_from dl $DL_FROM\r\";
	expect eof";
#fi
