set startDir=%CD%
cd ..\src\main\resources\assets\quiverchevsky\models
CALL "%startDir%\Ammo\CreateAmmoJSONs.bat"
cd %startDir%
pause