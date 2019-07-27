#!/usr/bin/env bash
echo "Deploy to Bintray"
mvn deploy -DskipTests
echo "Sync to Maven Central"
pom_version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)
echo Version $pom_version
result=$(curl -X POST -u $BINTRAY_USER:$BINTRAY_API_KEY https://api.bintray.com/maven_central_sync/martinfrancois/BoxplotFX/BoxplotFX/versions/$pom_version)
if [[ $result ==  *"Successfully synced and closed repo."* ]]
then
	echo Successfully synced to Maven Central
	exit 0
else
	echo Failed sync to Maven Central: $result
	exit 1
fi
