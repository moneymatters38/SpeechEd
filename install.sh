 #! /bin/bash
rm -rf build.sh
rm -rf bin/
android update project -n speechEd -t 2 -p .
ant debug
adb install -r bin/speechEd-debug.apk
