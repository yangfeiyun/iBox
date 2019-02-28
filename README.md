# iBox
A prototype of a Dropbox application with unit test

# What I did:

A prototype of Dropbox application utilizing Google Drive Java API.

The app monitors a specific directory and recursively checks main folder and subfolders for file creation, modification, and deletion.

(https://developers.google.com/drive/v3/web/quickstart/java)

# Environment Setup

Ubuntu 16.04

IntelliJ IDE with Maven project management tool

# How to run the program

1. Visit https://developers.google.com/drive/v3/web/quickstart/java to enable the API drive.

2. Download the configuration file and place it under src/main/resources.

3. Obtain the CLIENT info in the configuration file and replace them in GoogleDriveServiceProvider.java.

4. Run main.java with the monitored directory as the argument.

# Unit Test

Add JUnit dependency in pom.xml.

02/28: (not completed)

# Integration Test

Manual tests are done by adding, modifying, and deleting files in the monitored folder and subfolders (successful).

The app fails to sync new folder.

02/28: (automatic test not completed)

# Maven Test

Change to project directory and command line: mvn test

02/28: (successful)

# CircleCI

02/28: GitHub connection successful, yet automatic test failed.

# Code Coverage

1. Add Cobertura plugin in pom.xml.

2. Change to project directory and command line: mvn cobertura:cobertura

3. The report is target/site/cobertura/index.html (successful).

# Static Code Analysis

1. Add CheckStyle and FindBugs plugin in pom.xml.

2. Change to project directory and command line: mvn checkstyle:checkstyle / mvn findbugs:findbugs

3. Reports are under target/test-classes (successful).
