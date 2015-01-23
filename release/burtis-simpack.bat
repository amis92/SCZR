@ECHO OFF
chcp 65001
FOR %%A IN ("sync.SynchronizationModule","simulation.Simulation","passengers.PassengerModule") DO (
	START "%%A" java -cp burtis.jar -Dfile.encoding=UTF-8 burtis.modules.%%A
	TIMEOUT /t 1
)