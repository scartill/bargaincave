set BARGAINCAVE_HOME=%~dp0
set PYTHONPATH=%BARGAINCAVE_HOME%;%PYTHONPATH%
set PYTHONPATH=%BARGAINCAVE_HOME%\warehouse\amplify\backend\function\amplifyhelperslayer\lib\python\lib\python3.8\site-packages;%PYTHONPATH%
set AWS_PROFILE=bargaincave

call .secret.env
