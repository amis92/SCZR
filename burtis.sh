#!/bin/sh
for module in "network.server.DemoServerModule","sync.SynchronizationModule","simulation.Simulation","passengers.PassengerModule","busscheduler.BusSchedulerModule","gui.GuiModule"
do
	x-terminal-emulator -e java -cp burtis.jar burtis.modules.$module
done
