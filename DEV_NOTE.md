
TODO(high priority)
- what is the earliest point where a server is guaranteed to be available now or in the future
  - it's not `LifecycleEvent.SERVER_BEFORE_START`
  - it should be at or earlier then `CommandRegistrationEvent`, ot make command registering event be fired normally
  - 

arch api 1.22 -> 1.32
- villager trade
  	- VillagerTradeOfferContext
  	- WanderingTraderOfferContext
- item properties
  	- ItemPropertiesRegistry 
- Registries#forRegistry
  	- not sure about its usage
- LifecycleEvent.SETUP
  	- should be used for initializing server script manager earlier, otherwise command registering event will:
```
java.lang.NullPointerException: Cannot read field "scriptManager" because "dev.latvian.kubejs.server.ServerScriptManager.instance" is null
	at dev.latvian.kubejs.script.ScriptType.lambda$static$1(ScriptType.java:23)
	at dev.latvian.kubejs.event.EventJS.post(EventJS.java:31)
	at dev.latvian.kubejs.server.KubeJSServerEventHandler.registerCommands(KubeJSServerEventHandler.java:78)
```
- ClientLifecycleEvent for forge
- item as an additional context in ClientTooltipEvent
- container render events
	- GuiEvent.RENDER_CONTAINER_BACKGROUND
	- GuiEvent.RENDER_CONTAINER_FOREGROUND
- FakePlayers
