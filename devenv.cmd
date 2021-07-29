set BARGAINCAVE_HOME=%~dp0
set PYTHONPATH=%BARGAINCAVE_HOME%;%PYTHONPATH%
set PYTHONPATH=%BARGAINCAVE_HOME%\warehouse\amplify\backend\function\warehousecommonlayer\lib\python\;%PYTHONPATH%

call %BARGAINCAVE_HOME%\.secret.env
