if [ "$1" != "-s" ]
  then
    npm install;
    bower install;
fi;
ember build --output-path="../server/public"
#ember build --environment=production --output-path="../server/public"
