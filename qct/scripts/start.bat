@echo off
SET PATH=%PATH%;../native/windows
java -cp "../lib/*" -Djava.library.path="../native/windows" com.github.aleksikangas.qct.ui.QctApplication
