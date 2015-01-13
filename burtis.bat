@ECHO OFF
chcp 65001
FOR %%A IN ("network.server.DemoServerModule","simulation.Simulation","passengers.PassengerModule","sync.SynchronizationModule","busscheduler.BusSchedulerModule","gui.GuiModule") DO (
	START "%%A" java -cp burtis.jar -Dfile.encoding=UTF-8 burtis.modules.%%A
	TIMEOUT /t 1
)