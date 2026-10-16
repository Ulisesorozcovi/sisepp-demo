@REM Maven Wrapper script for Windows
@REM https://maven.apache.org/wrapper/

@IF "%__MVNW_ARG0__%"=="" (SET __MVNW_ARG0__=%~dpnx0) && SET __MVNW_PSMODULEP_SAVE=%PSModulePath% && SET PSModulePath=

@SETLOCAL
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET BASEDIR=%~dp0
@IF "%BASEDIR:~-1%"=="\" SET BASEDIR=%BASEDIR:~0,-1%
@SET MAVEN_WRAPPER_JAR=%BASEDIR%\.mvn\wrapper\maven-wrapper.jar
@SET MAVEN_WRAPPER_PROPERTIES=%BASEDIR%\.mvn\wrapper\maven-wrapper.properties

@REM Download wrapper jar if it doesn't exist
@IF NOT EXIST "%MAVEN_WRAPPER_JAR%" (
    @FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_WRAPPER_PROPERTIES%") DO (
        @IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
    )
    powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%MAVEN_WRAPPER_JAR%'"
)

@REM Find java.exe
@IF DEFINED JAVA_HOME (
    @SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
) ELSE (
    @SET JAVACMD=java.exe
)

"%JAVACMD%" ^
    %MAVEN_OPTS% ^
    %MAVEN_DEBUG_OPTS% ^
    -classpath "%MAVEN_WRAPPER_JAR%" ^
    "-Dmaven.multiModuleProjectDirectory=%BASEDIR%" ^
    %WRAPPER_LAUNCHER% %*

@IF "%MVNW_VERBOSE%"=="true" ECHO wrapper  cmd returnCode %ERRORLEVEL%
@IF %ERRORLEVEL% NEQ 0 GOTO error
@GOTO end

:error
@SET ERROR_CODE=%ERRORLEVEL%

:end
@ENDLOCAL & @SET ERROR_CODE=%ERROR_CODE%
@IF "%__MVNW_ARG0__%"=="" (SET PSModulePath=%__MVNW_PSMODULEP_SAVE%& SET __MVNW_PSMODULEP_SAVE=& SET __MVNW_ARG0__=)
@EXIT /B %ERROR_CODE%
