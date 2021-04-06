@echo off
setlocal

set DIR=%~dp0
set VM_OPTIONS=-cp lib\easy-crypto-0.1.0-all.jar

cd ..
start %DIR%javaw --add-modules java.desktop %VM_OPTIONS% com.example.zucker.easycrypto.MainKt

endlocal
